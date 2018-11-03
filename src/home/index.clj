(ns home.index
  (:require [coast]
            [clojure.string :as string]
            [clojure.data.json :as json]
            [clojure.java.shell :refer [sh]])
  (:import (java.io FileOutputStream FileInputStream)))

(defn view [request]
  [:div
   (coast/form (merge {:id "dropzone"
                       :class "dropzone w-100 h-100"
                       :style "border: none"
                       :enctype "multipart/form-data"}
                      (coast/action-for ::action))
    [:h1 {:class "f1-ns f3 tc"} "Photo Party 🎉"]
    [:div {:class "f3 tc mb2"} "Tap, click or drag anywhere"]
    [:div {:class "f6 mid-gray tc"} "(Except on the text)"]
    [:button {:id "submit-all" :style "cursor: pointer" :class "mv4 br1 dn w5 pv3 bg-blue white tc center bn"}
     "Upload"]
    [:span {:class "dz-message"}])])

(defn path [& args]
  (when (every? string? args)
    (->> (map string/trim args)
         (map #(string/replace % #"^/*|/*$" ""))
         (string/join "/")
         (str "/"))))

(defn thumbnail [{:keys [ident ext]}]
  (let [filename (str ident ext)
        thumb-name (str ident "-thumbnail" ext)]
    (sh "convert" "-define" "jpeg:size=500x500" filename "-thumbnail" "500x500^" "-gravity" "center" "-extent" "500x500" thumb-name
        :dir (path (coast/env :photos-path)))))

(defn ext [s]
  (re-find #"\..\w+$" s))

(defn save [m]
  (let [{:keys [tempfile filename]} m
        output-filename (path (coast/env :photos-path) (str (:ident m) (ext filename)))]
    (with-open [in (new FileInputStream tempfile)
                out (new FileOutputStream output-filename)]
      (let [source (.getChannel in)
            dest   (.getChannel out)]
        (.transferFrom dest source 0 (.size source))
        (.flush out)
        output-filename))))

(defn action [request]
  (let [files (->> (-> request :params :file vals)
                   (map #(assoc % :ident (str (coast/uuid))
                                  :ext (ext (:filename %)))))
        photos (map #(hash-map :photo/name (:filename %)
                               :photo/content-type (:content-type %)
                               :photo/ident (:ident %)
                               :photo/ext (:ext %)
                               :photo/size (:size %))
                    files)
        album-ident (str (coast/uuid))
        [_ errors] (coast/rescue
                     (coast/transact {:album/ident album-ident
                                      :album/photos photos}))]
    (if (nil? errors)
      (do
        (doseq [file files]
          (save file)
          (thumbnail file))
        (coast/ok {:album-ident album-ident} {"content-type" "application/json"}))
      (view (merge request errors)))))
