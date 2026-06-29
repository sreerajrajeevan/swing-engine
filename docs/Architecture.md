# SwingEngine Architecture

Version: 1.0

---

# 1. Purpose

SwingEngine is a Swing Trading Decision Engine.

Its responsibility is to analyze stocks using multiple independent engines and determine whether a stock should be bought, waited on, or rejected.

The architecture follows one important rule:

> Every engine has exactly one responsibility.

This makes the project easier to maintain, test, and improve.

---

# 2. High Level Architecture

                          +----------------------+
                          |   Market Data Engine |
                          +----------+-----------+
                                     |
                                     ▼
                          +----------------------+
                          | Technical Engine     |
                          +----------+-----------+
                                     |
                                     ▼
                          +----------------------+
                          | Fundamental Engine   |
                          +----------+-----------+
                                     |
                                     ▼
                          +----------------------+
                          | Chart Engine         |
                          +----------+-----------+
                                     |
                                     ▼
                          +----------------------+
                          | News Engine          |
                          +----------+-----------+
                                     |
                                     ▼
                          +----------------------+
                          | Risk Engine          |
                          +----------+-----------+
                                     |
                                     ▼
                          +----------------------+
                          | Decision Engine      |
                          +----------+-----------+
                                     |
                                     ▼
                          BUY / WAIT / NO TRADE

---

# 3. Project Structure

backend

    src/main/java

        com.sree.swingengine

            config/

            common/

            entity/

            repository/

            dto/

            market/

            technical/

            fundamentals/

            chart/

            news/

            risk/

            decision/

            scheduler/

            util/

Each package represents one business capability.

---

# 4. Responsibilities

## Market Engine

Responsibilities

- Download stock data
- Update database
- Schedule daily jobs

Output

- OHLCV Data

---

## Technical Engine

Responsibilities

- EMA
- RSI
- MACD
- ADX
- ATR
- Volume Analysis
- Support
- Resistance

Output

Technical Indicators

---

## Fundamental Engine

Responsibilities

- Revenue Growth
- Profit Growth
- ROE
- ROCE
- Debt
- Promoter Holding
- FII
- DII

Output

Fundamental Score

---

## Chart Engine

Responsibilities

- Trend Detection
- Consolidation Detection
- Breakout Detection
- Pullback Detection
- Pattern Detection
- Candlestick Detection

Output

Chart Score

---

## News Engine

Responsibilities

- Quarterly Results
- Corporate Actions
- Large Orders
- Insider Activity
- News Sentiment

Output

News Score

---

## Risk Engine

Responsibilities

- Entry
- Stop Loss
- Target
- Risk Reward
- Position Size

Output

Risk Score

---

## Decision Engine

Responsibilities

Receive results from every engine.

Combine them into one decision.

Possible decisions

BUY

WAIT

NO TRADE

The Decision Engine never calculates indicators.

It only evaluates the outputs produced by other engines.

---

# 5. Engine Flow

Market Engine

↓

Technical Engine

↓

Fundamental Engine

↓

Chart Engine

↓

News Engine

↓

Risk Engine

↓

Decision Engine

↓

Recommendation

Every engine is independent.

---

# 6. Database Overview

Main Tables

stocks

daily_prices

technical_indicators

fundamentals

news

recommendations

backtest_results

Additional tables may be added later.

---

# 7. Coding Principles

Every class must have one responsibility.

Avoid duplicate code.

Avoid large service classes.

Keep business logic inside services.

Repositories should contain only database access.

Controllers should never contain business logic.

---

# 8. Design Principles

Follow SOLID principles.

Follow Clean Code principles.

Prefer composition over inheritance.

Avoid premature optimization.

Write readable code.

Keep methods small.

---

# 9. Future Enhancements

These are intentionally excluded from Version 1.

- Authentication
- User Management
- Portfolio Tracking
- Telegram Integration
- Website Dashboard
- Mobile Application
- AI Chat Assistant

These features will only be implemented after the decision engine becomes reliable.

---

# 10. Project Philosophy

SwingEngine is NOT a stock screener.

SwingEngine is NOT a signal provider.

SwingEngine is a Decision Engine.

Its purpose is to reduce poor trading decisions by evaluating every trade through multiple independent validation engines.

The goal is quality over quantity.

If no trade qualifies,

the correct answer is

NO TRADE.

---

# 11. Development Order

Sprint 0

Architecture

↓

Sprint 1

Market Engine

↓

Sprint 2

Technical Engine

↓

Sprint 3

Fundamental Engine

↓

Sprint 4

Chart Engine

↓

Sprint 5

News Engine

↓

Sprint 6

Risk Engine

↓

Sprint 7

Decision Engine

↓

Sprint 8

Backtesting

↓

Sprint 9

AI

↓

Sprint 10

Dashboard

---

# 12. Success Criteria

The project is considered successful when it can consistently:

- Reject weak trade setups.
- Identify high probability swing trades.
- Explain every recommendation.
- Produce repeatable results.
- Improve through backtesting.

The engine must prioritize protecting capital over generating frequent trade recommendations.
