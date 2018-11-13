(ns album
  (:require [coast]))

(defn view [{{:keys [album-ident]} :params}]
  (let [album (coast/pull '[album/ident
                            {:album/photos [photo/ident photo/ext]}]
                          [:album/ident album-ident])]
    [:div {:class "mw7 center"}
     [:h1 {:class "f1-ns f3 tc"} "Photo Party 🎉"]
     [:div {:class "f3 tc mb2"} "Click photos to download them"]
     [:div {:class "cf ph4"}
      (for [{:photo/keys [ident ext]} (:album/photos album)]
       [:div {:class "fl w-third-ns w-50 pa2"}
        [:a {:href (str "/" ident ext)
             :download ""
             :class "grow no-underline"}
         [:img {:src (str "/" ident "-thumbnail" ext) :class "w-100"}]]])]]))
