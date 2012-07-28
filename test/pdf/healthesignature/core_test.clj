(ns pdf.healthesignature.core-test
  (:require [pdf.healthesignature.core :as pdf])
  (:use [midje.sweet]
        [pdf.healthesignature.helpers]
        [pdf.healthesignature.run]))

(def fields (:fields form))

(def expected-body
 [[:table {:border-width 0, :header [], :color [220 255 255]} 
    ["Date" "2012-07-26"] 
    ["Name" "Plankmank"] 
    ["Date of Birth" "2012-07-26"] 
    ["Previous Name" "A Chump"] 
    ["address" ""] 
    ["city" ""] 
    ["stage" ""] 
    ["zip" ""] 
    ["phone" ""] 
    ["doc" ""] 
    ["meds" ""] 
    ["slider1" "yes"] 
    ["slider2" "yes"] 
    ["hosp" ""] 
    ["slider3" "no"] 
    ["fam" ""] 
    ["slider4" "no"] 
    ["yes" "yes"]
    ]
    [:paragraph "I realize that a failure to disclosure accurate information or purposefully omitting information may result in denial of admission or discharge from the Center for Family Medicine once established. CFM is a Family Medicine Residency program; our goal is for the physicians in the Family Medicine Residency to complete our program and be skilled to continue providing excellent care for a wide range of patients within their own practice. We must have a diverse patient population in order to provide our physicians with the best experience. We must limit certain patient populations in order to accomplish these goals."] 
  [:image {:align :center :base64? true} signature]])

(fact "expand correctly partions the form"
  (pdf/expand fields pdf/break-on) => expected-body)

(fact "break-on split when ever it hits a signature"
  (let [parted (pdf/break-on fields)]
    (count parted) => 3))

(fact "break-on splits the vector correctly"
  (let [parted (pdf/break-on fields)]
    (count (first parted)) => 18
    (count (nth parted 1)) => 1))
