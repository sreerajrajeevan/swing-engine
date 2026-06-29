# SwingEngine

## Version

v1.0

---

# Overview

SwingEngine is an AI-assisted Swing Trading Decision Engine built to identify high-probability swing trade opportunities in the Indian stock market.

Unlike traditional stock screeners that only filter stocks based on technical indicators, SwingEngine performs multiple levels of analysis before making a recommendation.

The objective is **not to recommend a stock every day**, but to recommend a trade **only when all predefined conditions are satisfied**.

If no stock qualifies, the engine should return:

> **NO TRADE – STAY IN CASH**

---

# Vision

Build the most trusted Swing Trading Decision Engine for retail traders.

The engine should analyze hundreds of stocks, reject poor-quality setups automatically, and recommend only high-conviction swing trades.

Eventually this engine will power:

- Personal Trading Assistant
- Web Dashboard
- Mobile App
- Telegram Alerts
- Membership Platform

The first version is built exclusively for personal use.

---

# Project Goal

The primary goal is to improve trading decisions by removing emotion from stock selection.

Instead of asking:

> Which stock should I buy?

SwingEngine answers:

> Should I buy this stock today?

---

# Core Principles

The project follows these principles:

1. Simplicity over complexity.
2. Explain every recommendation.
3. Never force a trade.
4. Protect capital first.
5. Data-driven decisions only.
6. Every recommendation must be reproducible.
7. Risk management is mandatory.

---

# Engine Workflow

```
Market Data
      │
      ▼
Technical Analysis
      │
      ▼
Fundamental Analysis
      │
      ▼
Chart Analysis
      │
      ▼
News Analysis
      │
      ▼
Risk Analysis
      │
      ▼
Decision Engine
      │
      ▼
BUY / WAIT / NO TRADE
```

---

# Project Phases

## Phase 1

Market Data Engine

- Download market data
- Store OHLCV
- Scheduler

---

## Phase 2

Technical Engine

- EMA
- RSI
- MACD
- ADX
- ATR
- Volume Analysis
- Support
- Resistance

---

## Phase 3

Fundamental Engine

- Revenue Growth
- Profit Growth
- ROE
- ROCE
- Debt
- Promoter Holding
- Institutional Holdings

---

## Phase 4

Chart Intelligence Engine

- Trend Detection
- Consolidation Detection
- Breakout Detection
- Pullback Detection
- Pattern Recognition
- Candlestick Analysis

---

## Phase 5

News Engine

- Quarterly Results
- Corporate Actions
- Large Orders
- Block Deals
- Insider Activity
- News Sentiment

---

## Phase 6

Risk Engine

- Entry
- Stop Loss
- Targets
- Risk Reward
- Position Size

---

## Phase 7

Decision Engine

Combine every engine into a single decision.

Possible outputs:

- BUY
- WAIT
- NO TRADE

---

## Phase 8

AI Engine

Provide human-readable explanations for every recommendation.

---

# Initial Scope

Version 1 will only support:

- Nifty 500 Stocks
- Daily Timeframe
- Swing Trades
- Holding Period: 5–15 Trading Days

---

# Tech Stack

Backend

- Java 21
- Spring Boot 3
- Gradle
- PostgreSQL

Future

- React
- Docker
- AI Integration

---

# Project Structure

```
SwingEngine

docs/
    README.md
    Architecture.md

backend/

frontend/
```

---

# Development Philosophy

This project is not a stock screener.

It is a Swing Trading Decision Engine.

Every module exists for only one purpose:

> Improve the probability of selecting high-quality swing trades.

Features that do not improve trading decisions will not be added.

---

# Success Criteria

The engine is considered successful when it can consistently:

- Reject weak trade setups.
- Identify high-quality swing trade opportunities.
- Explain every recommendation.
- Produce repeatable and data-driven decisions.

---

# Future Vision

Future versions may include:

- Web Dashboard
- Portfolio Tracker
- Trade Journal
- Telegram Alerts
- AI Assistant
- Membership Platform

These features will only be developed after the decision engine has been validated through real trading.

---

# Author

Sree

Project Status:

🚧 Under Development