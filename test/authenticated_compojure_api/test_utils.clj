(ns authenticated-compojure-api.test-utils
  (:require [cheshire.core :as ch]
            [ring.mock.request :as mock]
            [authenticated-compojure-api.handler :refer [app]]
            [authenticated-compojure-api.queries.query-defs :as query]
            [buddy.core.codecs :as codecs]
            [buddy.core.codecs.base64 :as b64]))

(def str->base64 (comp codecs/bytes->str b64/encode))

(defn parse-body [body]
  (ch/parse-string (slurp body) true))

(defn basic-auth-header
  [request original]
  (mock/header request "Authorization" (str "Basic " (str->base64 original))))

(defn token-auth-header
  [request token]
  (mock/header request "Authorization" (str "Token " token)))

(defn get-user-token [username-and-password]
  (let [initial-response (app (-> (mock/request :get "/api/auth")
                                  (basic-auth-header username-and-password)))
        initial-body     (parse-body (:body initial-response))]
    (:token initial-body)))

(defn get-token-auth-header-for-user [request username-and-password]
  (token-auth-header request (get-user-token username-and-password)))

(defn get-permissions-for-user [id]
  (:permissions (query/get-permissions-for-userid query/db {:userid id})))

(defn add-users []
  (let [user-1 {:email "j@man.com" :username "JarrodCTaylor" :password "pass"}
        user-2 {:email "e@man.com" :username "Everyman"      :password "pass"}]
    (app (-> (mock/request :post "/api/user" (ch/generate-string user-1))
             (mock/content-type "application/json")))
    (app (-> (mock/request :post "/api/user" (ch/generate-string user-2))
             (mock/content-type "application/json")))))

(defn create-tables [f]
  (query/create-registered-user-table-if-not-exists! query/db)
  (query/create-permission-table-if-not-exists! query/db)
  (query/create-user-permission-table-if-not-exists! query/db)
  (query/create-password-reset-key-table-if-not-exists! query/db)
  (f))

