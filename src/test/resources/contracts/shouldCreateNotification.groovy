package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/v1/notifications'
        body([
                "notificationType"   : $(consumer(regex('(ERROR|WARN|INFO)')), producer('ERROR')),
                "notificationMessage": $(consumer(regex('.+')), producer('Your action failed.')),
                "userId"             : $(consumer(email()), producer('Your action failed.')),
        ])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 201
    }
    //createdAt          : value(producer(regex('[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{3}+0000'))),
}