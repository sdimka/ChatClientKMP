package dev.goood.chat_client

object Const {
    object Network {
//        const val API_ENDPOINT = "https://con.goood.dev:5050/"
        const val API_ENDPOINT = "http://213.199.42.236:5000/"
        const val REFRESH_TOKEN_ENDPOINT = "${API_ENDPOINT}api/get-auth-token"

        // Server-side upload limit (MAX_UPLOAD_MB on the backend).
        const val MAX_UPLOAD_MB = 20

    }
}
