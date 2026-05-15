# 🪙 Masroofy — مصروفي

> **Micro-budgeting for Egyptian students and young adults.**  
> Offline. Private. No bank connections. Just you and your cash.

---

## What Is Masroofy?

Masroofy (مصروفي — "my allowance") is an offline-first desktop budgeting app built for students who manage cash allowances on a weekly or monthly basis. No accounts. No internet. No fluff — just a clean interface that tells you exactly how much you can spend today without running out of money before your cycle ends.

It replaces the classic notebook-and-pen method with a smarter, faster digital alternative that fits the real life of Egyptian university students.

---

## Features

| Feature | Description |
|---|---|
| 💰 **Budget Cycle Setup** | Enter your total allowance and pick a start/end date. That's it. |
| 📊 **Safe Daily Limit** | Dynamically recalculates every day: *remaining balance ÷ remaining days*. |
| ⚡ **Rapid Expense Logging** | Log an expense in under 3 clicks — amount + category + save. |
| 🔄 **Smart Rollover** | Unspent funds from yesterday automatically boost tomorrow's limit. |
| 🥧 **Spending Dashboard** | Pie chart breakdown of your spending by category (Food, Transport, etc.). |
| 🔔 **Budget Alerts** | Local notification when you hit 80% of your total allowance. |
| 📜 **Transaction History** | Full chronological log, filterable by category and date. |
| ✏️ **Edit / Delete Expenses** | Fix mistakes. Daily limit auto-updates instantly. |
| 🔒 **Privacy Lock** | Optional 4-digit PIN or biometric lock to protect your data. |
| ♻️ **Cycle Reset** | Start fresh anytime — wipes all cycle data after confirmation. |
| ✈️ **100% Offline** | All data lives in a local SQLite database on your machine. Nothing is ever sent anywhere. |

---

## Screenshots

> <img width="1080" height="554" alt="image" src="https://github.com/user-attachments/assets/be6ad932-1629-4fc6-8e76-60d0c097ba83" />


---

## Tech Stack

- **Architecture:** MVC (Model-View-Controller)
- **Database:** MySQl (railway on-server)
- **Currency:** EGP (Egyptian Pound)
- **Platform:** Desktop (cross-platform)

---



### Installation

```bash
# Clone the repository
git clone https://github.com/your-org/masroofy.git
cd masroofy

# Install dependencies
Java fx


---

## How It Works

1. **First launch** — You're prompted to enter your total cash allowance and set a cycle start/end date.
2. **Daily use** — Open the app and see your Safe Daily Limit front and center. Tap "Log Expense" to record spending in under 3 taps.
3. **At midnight** — Unspent funds roll over automatically, adjusting your limit for the remaining days.
4. **Dashboard** — A pie chart shows where your money is going across categories like Food, Transport, and Entertainment.
5. **Alert** — When you've spent 80% of your allowance, a local notification fires to give you a heads-up.

---

## Core Concepts

**Safe Daily Limit**
```
Safe Daily Limit = Remaining Balance / Remaining Days
```
This recalculates whenever you log an expense, delete one, or a new day begins.

**Rollover Logic**
- Spent *less* than your limit yesterday? → Tomorrow's limit goes **up**.
- Spent *more* than your limit yesterday? → Tomorrow's limit goes **down** (shown in orange).

---

## Project Structure

```
masroofy/
├── src/
│   ├── models/          # SQLite database logic (cycles, transactions)
│   ├── views/           # UI screens (Dashboard, History, Settings, etc.)
│   ├── controllers/     # Business logic (limit calc, rollover, alerts)
│   └── main.js          # App entry point
├── assets/
│   └── icons/
├── database/
│   └── masroofy.db      # Local SQLite file (auto-created on first run)
├── package.json
└── README.md
```

---

## User Stories

The full requirements are documented in the [SRS (Software Requirements Specification)](./docs/SRS-v1.0.pdf). The app covers 12 user stories including:

- US#1 – Set Initial Budget Cycle
- US#2 – Rapid Expense Logging
- US#3 – Dynamic Daily Limit View
- US#4 – Visual Spending Insights
- US#5 – Daily Rollover Management
- US#6 – Budget Threshold Notification
- US#7 – Transaction History Review


---

## Non-Functional Requirements

- **Performance:** Expense logging completes in ≤ 3 clicks; dashboard updates in < 1 second.
- **Availability:** 100% functional offline — no internet required for any feature.
- **Security:** All data is stored locally only. Zero external API calls or server transmission.
- **Reliability:** SQLite journaling ensures data integrity on unexpected shutdowns.
- **Usability:** Designed for one-handed use and rapid entry on the go.

---

## Team

| Name | ID 
|---|---|---|
| Omar Tharwat | 20240365 
| Mohamed Hamdy | 20240492 
| Mohamed Foad | 20240522 
|Fathi Mostafa | 20242239 

**Course:** CS251 – Intro to Software Engineering  
**Faculty:** Faculty of Computers and Artificial Intelligence, Cairo University  
**Semester:** Spring 2026

---

## License

This project is for academic purposes as part of CS251 at Cairo University – FCAI.
