(ns clj-ptt-crawler.crawl-board
  (:require [clojure.string :as str]
            [net.cgrand.enlive-html :as enlive]
            [clj-http.client :as client])
  (:gen-class))

(def PTT_URL "https://www.ptt.cc")

(defn harvest-board-indices
  [url-board-index board-name]
  (->> (-> url-board-index
           (client/get {:insecure? true})
           (:body)
           (enlive/html-snippet)
           (enlive/select [[:a (enlive/attr-contains :href "/bbs/") (enlive/attr-contains :href "/index")]]))
       (map #(:href (:attrs %)))
       (filter #(not (.endsWith % "/index.html")))
       (map (fn [it] {:url it, :page_number (->> it (re-find #"\d+") Integer/parseInt) }))
       ))

(defn harvest-articles
  [url board-name]
  (->> (-> url
           (client/get {:insecure? true})
           (:body)
           (enlive/html-snippet)
           (enlive/select [[:a (enlive/attr-starts :href (.concat "/bbs/" board-name)) (enlive/attr-ends :href ".html")]]))
       (filter #(not (.contains (:href (:attrs %)) "/index")))
       (map (fn [it]
              (let [url (:href (:attrs it))
                    id  (->> url (re-find (re-pattern (str/join ["/bbs/" board-name "/(.+)\\.html"]))) second)]
                {:subject (:content it), :url url, :id id })))
       ))

(defn -main [board_name output_dir]
  (println (harvest-board-indices (clojure.string/join "" [PTT_URL "/bbs/" board_name "/index.html"]) board_name)))
