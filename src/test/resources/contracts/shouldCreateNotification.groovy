package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/v1/notifications'
        body([
                "notificationType"   : $(consumer(regex('(ERROR|INFO|WARN)')), producer('ERROR')),
                "notificationMessage": $(consumer(regex('.+')), producer('Your action failed.')),
                "userId"             : $(consumer(regex('.+')), producer('maki@example.com')),
        ])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 201
        headers {
            header 'Location': $(consumer('http://notification/v1/notifications/5d1f9fef-e0dc-4f3d-a7e4-72d2220dd827'), producer(regex('https?://.+/v1/notifications/[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}')))
        }
    }
}