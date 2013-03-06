(defproject pdf.healthesignature "1.0.9"
  :description "Takes the HealthESignature formatted JSON forms and converts them into pdfs"
  :main pdf.healthesignature.run
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.lowagie/itext "2.1.7"]
                 [org.clojure/data.json "0.1.3"]
                 [clj-pdf "0.9.7"]]
  :profiles {:dev {:dependencies [[midje "1.5-RC1"]]}})
