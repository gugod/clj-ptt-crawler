(ns clj-ptt-crawler.crawl-board
  (:import java.net.URL)
  (:require [clojure.string :as str]
            [net.cgrand.enlive-html :as enlive]
            [clj-http.client :as client])
  (:gen-class))

(def PTT_URL "https://www.ptt.cc")

(defn harvest-board-indices
  [url-board-index board-name]
  (-> url-board-index
      (client/get {:insecure? true})
      (enlive/html-snippet)
      (enlive/select [[:a (enlive/attr-contains :href (str/join ["/bbs/" board-name "/index"]) )]])))

(defn -main [board_name output_dir]
  (println (harvest-board-indices (clojure.string/join "" [PTT_URL "/bbs/" board_name "/index.html"]) board_name)))
