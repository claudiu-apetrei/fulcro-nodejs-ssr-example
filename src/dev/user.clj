(ns user
  (:require
   [clojure.tools.namespace.repl :as tools-ns :refer [set-refresh-dirs]]
   [fulcro-spec.suite :as suite]
   [fulcro-spec.selectors :as sel]))

;; === SHADOW REPL ===

(comment
 ;; evaluate any one of these in your nREPL to
 ;; choose a (running and connected) shadown-CLJS nREPL
 (do
   (require '[shadow.cljs.devtools.api :as shadow])
   (shadow/nrepl-select :server))

 (do
    (require '[shadow.cljs.devtools.api :as shadow])
    (shadow/nrepl-select :main))


 (do
   (require '[shadow.cljs.devtools.api :as shadow])
   (shadow/nrepl-select :test))

 (do
   (require '[shadow.cljs.devtools.api :as shadow])
   (shadow/nrepl-select :cards)))


;; ==================== SERVER ====================

;(set-refresh-dirs "src/dev" "src/main" "src/test")
;; currently broken in shadow-cljs
(set! *warn-on-infer* true)



