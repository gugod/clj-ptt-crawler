(ns clj-ptt-crawler.crawl-board
  (:require [clojure.string :as str]
            [net.cgrand.enlive-html :as enlive]
            [clj-http.client :as client])
  (:gen-class))

(def PTT_URL "https://www.ptt.cc")

(defn ptt-get [url]
  (:body (client/get url {:insecure? true :cookies {"over18" {:discard true, :path "/", :secure false, :value "1", :version 0}}})))

(defn harvest-board-indices
  [url-board-index board-name]
  (let [pn-minmax (->> (-> url-board-index
           (ptt-get)
           (enlive/html-snippet)
           (enlive/select [[:a (enlive/attr-contains :href "/bbs/") (enlive/attr-contains :href "/index")]]))
       (map #(:href (:attrs %)))
       (filter #(not (.endsWith % "/index.html")))
       (map (fn [it] (->> it (re-find #"\d+") Integer/parseInt))))]
    (map (fn [pn] {:page_number pn, :url (str/join [PTT_URL "/bbs/" board-name "/index" (str pn) ".html"]) })
         (range (first pn-minmax) (+ 1 (last pn-minmax))))))

(defn harvest-articles
  [url board-name]
  (->> (-> url
           (ptt-get)
           (enlive/html-snippet)
           (enlive/select [[:a (enlive/attr-starts :href (.concat "/bbs/" board-name)) (enlive/attr-ends :href ".html")]]))
       (filter #(not (.contains (:href (:attrs %)) "/index")))
       (map (fn [it]
              (let [href (:href (:attrs it))
                    id  (->> href (re-find (re-pattern (str/join ["/bbs/" board-name "/(.+)\\.html"]))) second)]
                {:subject (:content it), :url (.concat PTT_URL href), :id id })))))

(defn download-articles [articles board-name output-dir]
  (doseq [a articles]
    (let [output-file-name (str/join [output-dir "/" board-name "/" (:id a) ".html"])]
      (println "==> " output-file-name)
      (clojure.java.io/make-parents output-file-name)
      (spit output-file-name (ptt-get (:url a))))))

(defn -main [board_name output_dir]
  (doseq [x (->> (-> (clojure.string/join "" [PTT_URL "/bbs/" board_name "/index.html"])
                     (harvest-board-indices board_name))
                 (map (fn [it] (harvest-articles (:url it) board_name))))]
    (download-articles x board_name output_dir)))
