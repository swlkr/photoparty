(ns routes)

(def routes [[:get "/" :home/index :home]
             [:post "/" :home/action]
             [:get "/album/:album-ident" :album/view]
             [:get "/404" :home/not-found :404]
             [:get "/500" :home/server-error :500]])
