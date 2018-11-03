(ns routes)

(def routes [[:get "/" :home.index/view :home]
             [:post "/" :home.index/action]
             [:get "/album/:album-ident" :album.show/view]
             [:get "/404" :error.not-found/view :404]
             [:get "/500" :error.server-error/view :500]])
