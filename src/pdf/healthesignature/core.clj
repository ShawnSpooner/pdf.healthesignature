(ns pdf.healthesignature.core
  (:use 
    [pdf.healthesignature.run]
    [clj-pdf.core]))

(defn table [rows]
  (lazy-cat [:table {:border-width 0 :header [] :color [220 255 255]}] 
   rows))

(defmulti extract :type)

(defmethod extract :default [field] 
  [(:title field) (:value field)])

(defmethod extract "group" [field] 
  (lazy-cat [:table {:header [(:title field)]}] 
    (map extract (:elements field))))

(defmethod extract "text" [field]
   [:paragraph (:value field)])

(defmethod extract "signature" [field]
  [:table {:border false :cell-border false}
    [(:title field) [:image {:align :center :base64? true} (.substring (:value field) 22)]]])

;TODO it is possible that one of the grouped types will be alone and we need to account for that
(defmethod extract nil [field]
  (if (> (count field) 1) 
    (table (map extract field))
    (extract (first field))))

(defn partition-when [pred coll]
  (lazy-seq
    (when-let [[x & xs] (seq coll)]
      (let [[xs ys] (split-with (complement pred) xs)]
        (cons (cons x xs) (partition-when pred ys))))))


(defn break-on [fields]
  (partition-when #(or 
                   (=(:type %) "text")
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

(def fields (:fields form))

(defn build []
  (let [{fields :fields name :name description :description user-id :userId} form
        pdf-body (cons 
             {:title  name
             :size          :a4
             :orientation   :potrait
             :subject description
             :author user-id
             :creator "healthesignature"
             :font {:size 11 :family :helvetica}
             :doc-header ["inspired by" "William Shakespeare"]
             :header "CFM Admission"
             :footer "HealthESignature"
             }
             (cons (logo)
               (expand fields break-on)))]
  (println (count pdf-body))
  (pdf 
    pdf-body
    "test.pdf")))
