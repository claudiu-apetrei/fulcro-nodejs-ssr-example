(ns fulcro-nodejs-ssr-example.development-preload
  (:require [fulcro.logging :as log]))

; Add code to this file that should run when the initial application is loaded in development mode.
; shadow-cljs already enables console print and plugs in devtools if they are on the classpath,

(js/console.log "Dev preload code)")
(log/set-level! :all)

