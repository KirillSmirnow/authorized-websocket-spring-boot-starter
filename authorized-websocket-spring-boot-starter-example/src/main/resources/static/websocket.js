import {Client} from "@stomp/stompjs"

const postsSubscriptions = []
const messagesSubscriptions = []
const miscSubscriptions = []

const configureListeners = () => {
    document.getElementById("signedInUser").onchange = () => {
        updatePostsSubscription()
        updateMessagesSubscription()
    }
    document.getElementById("postsSubscribed").onchange = updatePostsSubscription
    document.getElementById("postsUser").onchange = updatePostsSubscription
    document.getElementById("messagesSubscribed").onchange = updateMessagesSubscription
    document.getElementById("messagesUser").onchange = updateMessagesSubscription
    document.getElementById("AliceBobChat").onchange = updateMessagesSubscription
    document.getElementById("AliceChat").onchange = updateMessagesSubscription
    document.getElementById("BobChat").onchange = updateMessagesSubscription
    document.getElementById("miscSendButton").onclick = sendMessage
    document.getElementById("miscSubscribeButton").onclick = subscribeTo
}

const updatePostsSubscription = () => {
    postsSubscriptions.forEach((subscription) => subscription.unsubscribe())
    postsSubscriptions.splice(0, postsSubscriptions.length)

    const subscribed = document.getElementById("postsSubscribed").checked
    if (subscribed) {
        const user = document.getElementById("postsUser").value
        const path = `/users/${user}/posts`
        const subscription = client.subscribe(path, frame => {
            const post = JSON.parse(frame.body)
            const container = document.getElementById("postsContainer")
            const element = document.createElement("div")
            element.innerHTML = `<hr/><p>For ${user}</p><b>${post.title}</b><p>${post.text}</p>`
            container.appendChild(element)
            container.scrollTop = container.scrollHeight
        }, getHeaders())
        postsSubscriptions.push(subscription)
    }
}

const updateMessagesSubscription = () => {
    messagesSubscriptions.forEach((subscription) => subscription.unsubscribe())
    messagesSubscriptions.splice(0, messagesSubscriptions.length)

    const subscribed = document.getElementById("messagesSubscribed").checked
    if (subscribed) {
        const user = document.getElementById("messagesUser").value
        for (let chat of ["AliceBobChat", "AliceChat", "BobChat"]) {
            if (document.getElementById(chat).checked) {
                const path = `/users/${user}/chats/${chat}/messages`
                const subscription = client.subscribe(path, frame => {
                    const message = JSON.parse(frame.body)
                    const container = document.getElementById("messagesContainer")
                    const element = document.createElement("div")
                    element.innerHTML = `<hr/><p>For ${user} in ${chat}</p><p>${message.text}</p>`
                    container.appendChild(element)
                    container.scrollTop = container.scrollHeight
                }, getHeaders())
                messagesSubscriptions.push(subscription)
            }
        }
    }
}

const sendMessage = () => {
    const path = document.getElementById("miscSendInput").value
    client.publish({
        destination: path,
        body: JSON.stringify({title: "FORBIDDEN!", text: "This message must NOT be sent!!!"}),
    })
}

const subscribeTo = () => {
    miscSubscriptions.forEach((subscription) => subscription.unsubscribe())
    miscSubscriptions.splice(0, miscSubscriptions.length)

    document.getElementById("miscContainer").innerHTML = ""
    const path = document.getElementById("miscSubscribeInput").value
    const subscription = client.subscribe(path, frame => {
        const container = document.getElementById("miscContainer")
        const element = document.createElement("div")
        element.innerHTML = `<hr/>${frame}`
        container.appendChild(element)
        container.scrollTop = container.scrollHeight
    }, getHeaders())
    miscSubscriptions.push(subscription)
}

const getHeaders = () => {
    const user = document.getElementById("signedInUser").value
    return {
        "access-token": `${user}Token`
    }
}

const client = new Client({
    brokerURL: "ws://localhost:8080/websocket",
    onConnect: () => {
        updatePostsSubscription()
        updateMessagesSubscription()
    },
    onStompError: (frame) => {
        const container = document.getElementById("miscContainer")
        const element = document.createElement("div")
        element.innerHTML = `<hr/>${frame}`
        container.appendChild(element)
        container.scrollTop = container.scrollHeight
    },
})

configureListeners()
client.activate()
