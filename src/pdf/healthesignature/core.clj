;   Copyright (c) Shawn Spooner. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns pdf.healthesignature.core
  (:use 
    [clj-pdf.core])
  (:import 
    [com.lowagie.text Rectangle]
    [com.lowagie.text.pdf PdfSignatureAppearance PdfReader PdfStamper]
    [java.io ByteArrayOutputStream]))

(defn table [rows]
  (lazy-cat [:table {:border-width 0 :header [] :color [163 198 218]}] 
   rows))

(defn pair [field] 
  [(:title field) (:value field)])

(defmulti extract :type)

(defmethod extract :default [field] 
  (table [(pair field)]))

(defmethod extract "group" [field] 
  (lazy-cat [:table {:header [(:title field)]}] 
    (map pair (:elements field))))

(defmethod extract "text" [field]
   [:paragraph (:value field)])

(defn build-image [field]
  [:image {:align :center :base64? true} 
   (.substring (:value field) 22)])

(defn titled-image [field]
  [:table {:border false :cell-border false}
    [(:title field) [:image {:align :center :base64? true} (.substring (:value field) 22)]]])

(defmethod extract "signature" [field]
  (titled-image field))

(defmethod extract "freeDraw" [field]
  (build-image field))

(defmethod extract "multiSelect" [field]
 [:paragraph
   [:chunk {:style :bold} (:name field)]
   (lazy-cat [:list {:roman false}] 
     (:value field))])

(defmethod extract nil [field]
  (if (> (count field) 1) 
    (table (map pair field))
    (extract (first field))))

;TODO this really needs to be cleaned up
(defn b [pred x xs]
  (if (pred x)
    [x xs]
    (let [[matched non] (split-with (complement pred) xs)]
      [(cons x matched) non])))

(defn partition-when [pred coll]
  (let [step (fn [p c]
    (when-let [[x & xs] (seq c)]
      (let [[h tail] (b pred x xs)]
        (if (seq h)
          (cons h (partition-when pred tail))
          (recur pred tail)))))]
    (lazy-seq (step pred coll))))
;TODO

(defn break-on [fields]
  (partition-when #(or 
                   (=(:type %) "text")
                   (=(:type %) "multiSelect")
                   (=(:type %) "group")
                   (=(:type %) "freeDraw")
                   (=(:type %) "signature"))
                fields))

(defn expand [fields part-by]
  (let [partitioned (part-by fields)]
    (interpose [:spacer] (map extract partitioned))))

(defn logo []
  [:image {:align :left
           :xscale 0.3
           :yscale 0.3} 
    "SigLogo.png"])

;for now just add a field to the end of the document and sign
(defn sign [bytes key chain]
 (let [output (new ByteArrayOutputStream)]
   (with-open [stamper (PdfStamper/createSignature (new PdfReader bytes) output \0)]
     (doto (.getSignatureAppearance stamper)
       (.setCrypto key, chain, nil, PdfSignatureAppearance/SELF_SIGNED)
       (.setReason "HIPAA Compliance.")
       (.setLocation "SigMed")
       (.setCertificationLevel PdfSignatureAppearance/CERTIFIED_NO_CHANGES_ALLOWED)))
     output))

(defn build [form]
  (let [{fields :fields name :name description :description user-id :userId} form
        out (new ByteArrayOutputStream)
        pdf-body (cons 
             {:title  name
             :size          :a4
             :orientation   :potrait
             :subject description
             :author user-id
             :creator "healthesignature"
             :font {:size 11 :family :helvetica}
             :header name
             :footer "HealthESignature"
             }
             (cons (logo)
               (expand fields break-on)))]
    (pdf pdf-body out)
    out))
