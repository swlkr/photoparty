(ns cron
  (:require [coast]
            [coast.time]
            [clojure.java.io :as io]))

(defn -main [& args]
  (let [albums (->> (coast/q '[:pull [album/id album/created-at
                                      {:album/photos [photo/id photo/ident photo/ext]}]])
                    (filter #(> (-> % :album/created-at coast.time/since :seconds) 10)))]
    (println albums)
    (when (not (empty? albums))
      (let [photos (mapcat :album/photos albums)
            files (map #(str (coast/env :photos-path) (:photo/ident %) (:photo/ext %)) photos)
            thumbnails (map #(str (coast/env :photos-path) (:photo/ident %) "-thumbnail" (:photo/ext %)))]
        (doseq [file files]
          (when (true? (-> file io/file .exists))
            (io/delete-file file)))
        (coast/delete (map #(select-keys % [:album/id]) albums))))))

(cron/-main)
