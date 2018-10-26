(ns fulcro-nodejs-ssr-example.client
  (:require [fulcro.client :as fc]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [fulcro.client.routing :as r :refer-macros [defrouter]]
            [fulcro.client.data-fetch :as df]
            [fulcro.server-render :as ssr]
            [fulcro-nodejs-ssr-example.ui.root :as root]
            [fulcro.i18n :as i18n]
            ["intl-messageformat" :as IntlMessageFormat]
            [fulcro.client.network :as fcn]))


(defonce app (atom nil))

(defn mount []
  (reset! app (fc/mount @app root/Root "app")))


(defn start []
  (mount))

(defn ^:export init []
  (reset! app (fc/new-fulcro-client
               :networking {:remote (fcn/fulcro-http-remote {:url "http://localhost:3000/api"})}
               :initial-state (atom (ssr/get-SSR-initial-state))
               ))
  (start))
