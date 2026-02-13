# 🔌 Real-Time WebSocket API

This system enables **real-time bus tracking and public location sharing** using **STOMP over WebSocket** between:

* 🚍 **Driver (Android App)**
* 📱 **Client (Android / PWA)**
* 🧠 **Spring Boot Backend**

The backend acts as a **central message broker**, enforcing driver ownership per bus and broadcasting updates to subscribed clients.

---

## 🌐 WebSocket Connection

### 🔗 Connection URL

```
ws://<server-ip>:<port>/<context-path>/ws
```

### Example

```
ws://192.168.240.184:8080/ps/ws
```

---

## 🔐 Required Connection Headers

| Header  | Required | Description                                  |
| ------- | -------- | -------------------------------------------- |
| `iam`   | ✅ Yes    | `"sender"` (driver) or `"receiver"` (client) |
| `email` | ✅ Yes    | Email of connecting user                     |
| `busId` | ✅ Yes    | Bus identifier                               |

### Example (Driver)

```
iam: sender
email: driver@example.com
busId: 1
```

### Example (Client)

```
iam: receiver
email: client@example.com
busId: 1
```

---

# 📡 Messaging Endpoints

---

## 🚍 1. Driver Location Updates

Drivers publish bus movement in real time.

### ➜ Send To

```
/app/bus-location
```

### ➜ Broadcasted To

```
/topic/bus-location/{busId}
```

### 📦 Payload

```json
{
  "userEmail": "driver@example.com",
  "busId": 1,
  "lat": 19.0760,
  "lng": 72.8777
}
```

### ⚙️ Server Behavior

* Stores latest bus location (LocationService / BusControlService)
* Broadcasts to all subscribers of the specific bus topic
* Sends Discord notifications only on driver connect/disconnect (not per update)
* Enforces single active driver per bus

---

## 📍 2. Public Discovery Location (Snapchat-Style Pin)

Clients can drop public discovery pins visible to all users.

### ➜ Send To

```
/app/public-location
```

### ➜ Broadcasted To

```
/topic/public-users
```

### 📦 Payload

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "lat": 19.0760,
  "lng": 72.8777,
  "timestamp": 1670000000000
}
```

### ⚙️ Server Behavior

* Server assigns/validates `timestamp`
* Broadcasts to all subscribers of `/topic/public-users`

---

# 📥 Subscription Endpoints

---

## 🚌 Bus Location Subscription

### Subscribe To

```
/topic/bus-location/{busId}
```

### Example

```
/topic/bus-location/1
```

### Payload Received

```json
{
  "userEmail": "driver@example.com",
  "busId": 1,
  "lat": 19.0760,
  "lng": 72.8777
}
```

Clients receive real-time driver updates.

---

## 🌍 Public Users Subscription

### Subscribe To

```
/topic/public-users
```

### Payload Received

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "lat": 19.0760,
  "lng": 72.8777,
  "timestamp": 1670000000000
}
```

All clients receive discovery pins in real time.

---

# 🔔 Connection Lifecycle Events (Discord Observability)

The system sends operational alerts to Discord for important events.

### 🟢 Driver Connected

```
Driver driver@example.com started sharing location for Bus 1
```

### 🔴 Driver Disconnected

```
Driver driver@example.com stopped sharing location for Bus 1
```

### 🔵 Client Connected

```
Client client@example.com connected to Bus 1
```

### ⚪ Client Disconnected

```
Client disconnected: session=<sessionId>
```

### ⚠️ Driver Conflict

If a bus already has an active driver:

```
Driver driver@example.com tried to connect but Bus 1 already has an active driver.
```

The connection is rejected.

---

# 🧠 Architectural Characteristics

* STOMP over WebSocket
* Event-driven real-time architecture
* Topic-based message broadcasting
* Single-driver-per-bus enforcement
* Multi-client subscription model
* Operational observability via Discord alerts
* Android + PWA real-time synchronization

---

# 🏗 High-Level Flow

```
Driver → /app/bus-location → Server → /topic/bus-location/{busId} → Clients
Client → /app/public-location → Server → /topic/public-users → All Clients
```

---
