(ns fulcro-nodejs-ssr-example.server
  (:require
   [fulcro.client.primitives :as prim :refer [defsc]]
   [fulcro.client.data-fetch :as df]
   [fulcro.client :as fc]
   [fulcro.client.network :as fcn]
   [fulcro.server-render :refer [defer-until-network-idle]]
   [fulcro.client.util :refer [transit-clj->str transit-str->clj]]
   [hiccups.runtime :as hiccupsrt]
   [fulcro-nodejs-ssr-example.ui.root :as root]
   ["react-dom/server" :as rdom :refer (render)]
   ["body-parser" :as bp]
   ["xmlhttprequest" :as npm-xmlhttprequest]
   ["express" :as express]))

(set! js/global.XMLHttpRequest (.-XMLHttpRequest npm-xmlhttprequest))

(defonce server (atom {}))
(def ui-root (prim/factory root/Root))

(defn get-html [app-html app-state]
  (str
   "<!DOCTYPE html>"
   (hiccupsrt/render-html
    [:html
     [:head
      [:meta {:charset "UTF-8"}]
      [:title "test"]]
     [:body
      [:script (str "window.INITIAL_APP_STATE = '" (transit-clj->str app-state) "'")]
      [:div#app app-html]
      [:script {:src "js/main/app.js"}]
      [:script "fulcro_nodejs_ssr_example.client.init();"]]])))


(def dummy-resp-data [{:db/id 1, :kind :person/by-id, :person/name "Tony"}
                      {:db/id 2, :kind :thing/by-id, :thing/label "Toaster"}
                      {:db/id 3, :kind :place/by-id, :place/name "New York"}
                      {:db/id 4, :kind :person/by-id, :person/name "Sally"}
                      {:db/id 5, :kind :thing/by-id, :thing/label "Pillow"}
                      {:db/id 6, :kind :place/by-id, :place/name "Canada"}])

(defn api-endpoint [req res]
  (let [query     (-> (.-body req) str transit-str->clj)    ; parse query, not used now
        rand-time (rand-nth [100 400 200 500 1000 2000])
        resp      {:items dummy-resp-data}
        x-trans   (transit-clj->str resp)]
    (.send res x-trans)
    #_(js/setTimeout (fn []) rand-time) ;wrap .send in set timeout for extra latency
    ))




(defn ssr-enpdpoint [req resp]
  (let [started-callback (fn [{:keys [reconciler]}]
                           (df/load reconciler :items root/ItemUnion {:target [:lists/by-id :singleton :items]}))
        fulcro-client    (fc/new-fulcro-client
                          :started-callback started-callback
                          :networking {:remote (fcn/fulcro-http-remote {:url "http://localhost:3000/api"})})
        mounted-app      (fc/mount fulcro-client root/Root nil)
        reconciler       (:reconciler mounted-app)]
    (defer-until-network-idle
     reconciler
     (fn []
       (let [app-state (-> reconciler deref)
             app-state-tree        (prim/db->tree (prim/get-query root/Root app-state) app-state app-state)]
         (.send resp (get-html (rdom/renderToString (ui-root app-state-tree)) app-state))))
     )))



(defn start-server []
  (let [app (express)]
    (.use app (bp/raw #js{"type" "application/transit+json"}))
    (.get app "/" ssr-enpdpoint)
    (.post app "/api" api-endpoint)
    (.use app (.static express "resources/public"))
    (.listen app 3000 (fn [] (println "Example app listening on port 3000!")))))



(defn start! []
  ;; called by main and after reloading code
  (reset! server (start-server)))

(defn stop! []
  ;; called before reloading code
  (.close @server)
  (reset! server nil))


(defn main []
  ;; executed once, on startup, can do one time setup here
  (start!))







