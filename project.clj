(defproject pdf.healthesignature "1.0.8"
  :description "Takes the HealthESignature formatted JSON forms and converts them into pdfs"
  :main pdf.healthesignature.run
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [com.lowagie/itext "2.1.7"]
                 [org.clojure/data.json "0.1.3"]
                 [midje "1.4.0"]
                 [clj-pdf "0.9.7"]]
  :dev-dependencies [[midje "1.4.0"]])
