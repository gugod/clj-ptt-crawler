(ns clj-ptt-crawler.convert-articles-html
  (:require [clojure.data.json :as json]
            [net.cgrand.enlive-html :as enlive])
  (:gen-class))

(defn html-only [f] (and (.isFile f) (re-find #"\.html$" (.getName f))))

(defn convert-this [f]
  (let [output-file-name (.replace (.getPath f) ".html" ".json")]
    (prn "==> " output-file-name)
    (-> f
        slurp
        enlive/html-snippet
        ((fn [dom]  {:dom (enlive/select dom [[:div (enlive/attr-contains :id "main-content")]])}))
        ((fn [article]
           ;; (enlive/select (:dom article) [[:div (enlive/attr-contains :class "article-metaline")]])
           {:meta [ (enlive/select (:dom article) [:div.article-metaline] ) ],
            :push [ (enlive/select (:dom article) [:div.push ] ) ],
            :body (enlive/select (:dom article) [:div#main-content])}
           ))
        prn
        ;;   (for [x (enlive/select dom [[:div (enlive/attr-contains :class "article-metaline")]])]
        ;;     [(enlive/select x [[:span (enlive/attr-contains :class "article-meta-tag")]])
        ;;      (enlive/select x [[:span (enlive/attr-contains :class "article-meta-value")]])]))
        )))

(defn -main [ptt_dir]
  (let [files (filter html-only (file-seq (clojure.java.io/file ptt_dir)))]
    (doseq [f files]
      (convert-this f))))

