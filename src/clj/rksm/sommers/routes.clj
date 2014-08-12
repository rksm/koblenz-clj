(ns rksm.sommers.routes
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [compojure.core :as comp]))

(defn base [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello!"})

(comp/defroutes routes
  (comp/GET "/" [] base)
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site routes)))
