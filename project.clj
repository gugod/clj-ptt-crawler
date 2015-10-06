(defproject clj-ptt-crawler "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "CC0"
            :url "http://creativecommons.org/publicdomain/zero/1.0/"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "2.0.0"]
                 [enlive "1.1.6"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
