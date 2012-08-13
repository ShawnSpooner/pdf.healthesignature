(defproject pdf.healthesignature "1.0"
  :description "Takes the HealthESignature formatted JSON forms and converts them into pdfs"
  :main pdf.healthesignature.run
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [com.lowagie/itext "2.1.7"]
                 [clj-pdf "0.9.7"]]
  :dev-dependencies [[midje "1.4.0"]])
