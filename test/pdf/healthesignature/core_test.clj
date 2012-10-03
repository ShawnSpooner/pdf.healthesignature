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
    ["Previous Name if applicable" "A Chump"] 
    ["Address" ""] 
    ["City" ""] 
    ["State" ""] 
    ["Zip Code" ""] 
    ["Phone number to reach you" ""] 
    ["Name of current or most recent Doctor" ""] 
    ["List of Current Medications and Prescriber" ""] 
    ["Are you and EMMC employee or spouse of an employee" "yes"] 
    ["Have you been hospilalized within the past year?" "yes"] 
    ["If yes, name of Hospitol" ""] 
    ["Currently being seen for a Workers Compensations Injury?" "no"] 
    ["Family Members current patients at Center Form Family Medicene" ""] 
    ["Have you ever been deisharged from any Eastern Main Medical Physician Office?" "no"] 
    ]
    [:spacer]
    [:paragraph "I am setting off the next piece of information"]
    [:spacer]
    [:table {:border-width 0, :header [], :color [220 255 255]} 
      ["If yes, reason why" "yes"]]
      [:spacer]
  [:table {:border false :cell-border false}
   ["Patient Signature First" [:image {:align :center :base64? true} signature]]]
  [:spacer]
    [:paragraph "I realize that a failure to disclosure accurate information or purposefully omitting information may result in denial of admission or discharge from the Center for Family Medicine once established. CFM is a Family Medicine Residency program; our goal is for the physicians in the Family Medicine Residency to complete our program and be skilled to continue providing excellent care for a wide range of patients within their own practice. We must have a diverse patient population in order to provide our physicians with the best experience. We must limit certain patient populations in order to accomplish these goals."] 

  [:spacer]
  [:table {:border false :cell-border false}
   ["Patient Signature" [:image {:align :center :base64? true} signature]]]])

(fact "expand correctly partions the form"
  (pdf/expand fields pdf/break-on) => expected-body)

(fact "break-on split when ever it hits a signature"
  (let [parted (pdf/break-on fields)]
    (count parted) => 6))

(fact "break-on splits the vector correctly"
  (let [parted (pdf/break-on fields)]
    (count (first parted)) => 17
    (count (nth parted 1)) => 3))
