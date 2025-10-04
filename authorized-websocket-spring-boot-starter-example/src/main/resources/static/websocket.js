import {Client} from "@stomp/stompjs"

const postsSubscriptions = []
const messagesSubscriptions = []

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
        })
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
                })
                messagesSubscriptions.push(subscription)
            }
        }
    }
}

const client = new Client({
    brokerURL: "ws://localhost:8080/websocket",
    onConnect: () => {
        updatePostsSubscription()
        updateMessagesSubscription()
    },
})

configureListeners()
client.activate()
