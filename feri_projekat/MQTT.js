const mqtt = require('mqtt')
var NotificationModel = require('./models/notificationModel.js');
const {json} = require("express");

const client  = mqtt.connect('mqtt://broker.emqx.io')

client.on('connect', function () {
    client.subscribe('testtopic/straylight/#', function (err) {
        if (!err) {
            client.publish('testtopic/straylight', 'Hello mqtt from Server Lea ')
        }
    })
})

client.on('message', function (topic, message) {
    // message is Buffer
    console.log(message.toString() + topic)

    try {

        /*
 {
  "content": "Teeeest455599999",
  "latitude": "56464",
  "longitude": "77777"
}
        * */
        var data = JSON.parse(message.toString());

        var Notification = new NotificationModel({
            content : data.content,
            topic : topic,
            latitude : data.latitude,
            longitude : data.longitude
        });

        Notification.save(function (err, Notification) {

        });
    } catch (e) {
        return false;
    }



    //client.end()
})