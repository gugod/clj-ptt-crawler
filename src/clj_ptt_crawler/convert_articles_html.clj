(ns clj-ptt-crawler.convert-articles-html
  (:require [clojure.string :as string]
            [clojure.data.json :as json]
            [net.cgrand.enlive-html :as enlive])
  (:gen-class))

(defn html-only [f] (and (.isFile f) (re-find #"\.html$" (.getName f))))

(defn extract-text-content [dom sel]
  (first (enlive/select dom [sel :> enlive/text-node ])))

(defn extract-meta-tag-value [dom]
  (->> (enlive/select dom [:div.article-metaline])
      (map
       (fn [mt]
         [(extract-text-content mt :span.article-meta-tag),
          (extract-text-content mt :span.article-meta-value)]))))

(defn extract-push [dom]
  (->> (enlive/select dom [:div.push])
       (map
        (fn [pt] {
                  :tag        (extract-text-content pt :span.push-tag),
                  :userid     (extract-text-content pt :span.push-userid),
                  :content    (extract-text-content pt :span.push-cotnent),
                  :ipdatetime (extract-text-content pt :span.push-ipdatetime),
                  }))))

(defn extract-article-body [dom]
  (string/join "" (enlive/select (enlive/at dom [:div.article-metaline-right] nil [:div.article-metaline] nil [:div.push] nil)
                                 [:* :> enlive/text-node])))

(defn extract-article [dom]
  {:meta (extract-meta-tag-value dom),
   :push (extract-push dom),
   :body (extract-article-body dom)})

(defn convert-this [f]
  (let [output-file-name (.replace (.getPath f) ".html" ".json")
        article-dom (enlive/select (enlive/html-snippet (slurp f)) [:div#main-content])
        article-json (json/write-str (extract-article article-dom) :escape-unicode nil)]
    (spit output-file-name article-json)
    (println "==> " output-file-name)))

(defn -main [ptt_dir]
  (let [files (filter html-only (file-seq (clojure.java.io/file ptt_dir)))]
    (doseq [f files]
      (convert-this f))))
