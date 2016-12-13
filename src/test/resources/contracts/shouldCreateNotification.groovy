package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/v1/notifications'
        body("""
            {
                "notificationType":"ERROR", 
                "notificationMessage":"Your action failed", 
                "userId":"maki@example.com"
            }
        """)
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 201
        body([
                notificationType   : "ERROR",
                notificationMessage: "Your action failed",
                userId             : "maki@example.com"])
        headers {
            contentType("application/hal\\+json;charset=UTF-8")
        }
    }
    //createdAt          : value(producer(regex('[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{3}+0000'))),
}