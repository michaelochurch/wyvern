(ns wyvern.core
  (:require [ring.adapter.jetty :use [run-jetty]]))

(defn app [req]
  {:status 200 :body req})

(def server (run-jetty #'app {:port 1717 :join? false}))
