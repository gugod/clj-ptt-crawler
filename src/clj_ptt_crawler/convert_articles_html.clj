(ns clj-ptt-crawler.convert-articles-html
  (:gen-class))

(defn html-only [f] (and (.isFile f) (re-find #"\.html$" (.getName f))))

(defn -main [ptt_dir]
  (let [files (filter html-only (file-seq (clojure.java.io/file ptt_dir)))]
    (doseq [f files]
      (println (.getName f)))))

