(ns authenticated-compojure-api.route-functions.auth.get-auth-credentials
  (:require [authenticated-compojure-api.general-functions.user.create-token :refer [create-token]]
            [authenticated-compojure-api.queries.query-defs :as query]
            [ring.util.http-response :as respond]))

(defn auth-credentials-response
  "Generate response for get requests to /api/v1/auth. This route requires basic
   authentication. A successful request to this route will generate a new
   refresh-token, and return {:id :username :permissions :token :refreshToken}"
  [request]
  (let [user          (:identity request)
        refresh-token (str (java.util.UUID/randomUUID))
        _             (query/update-registered-user-refresh-token! query/db {:refresh_token refresh-token :id (:id user)})]
    (respond/ok {:id            (:id user)
                 :username      (:username user)
                 :permissions   (:permissions user)
                 :token         (create-token user)
                 :refreshToken  refresh-token})))
