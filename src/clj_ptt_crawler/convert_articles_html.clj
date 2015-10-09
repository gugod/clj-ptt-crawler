(ns clj-ptt-crawler.convert-articles-html
  (:require [clojure.data.json :as json]
            [net.cgrand.enlive-html :as enlive])
  (:gen-class))

(defn html-only [f] (and (.isFile f) (re-find #"\.html$" (.getName f))))

(defn convert-this [f]
  (let [output-file-name (.replace (.getPath f) ".html" ".json")
        dom (enlive/html-snippet (slurp f))]
    (prn
     "==> " output-file-name
     (enlive/at
      (enlive/select dom [:div#main-content])
      [:div.article-metaline] (enlive/content)
      [:div.push] (enlive/content)
      ))))

(defn -main [ptt_dir]
  (let [files (filter html-only (file-seq (clojure.java.io/file ptt_dir)))]
    (doseq [f files]
      (convert-this f))))

