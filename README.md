# 🎄📦 KringleCrate

## Overview

KringleCrate is a Minecraft plugin designed to enable a Secret Santa gift exchange among players. It features an opt-in system, recipient assignments, gift submissions, and a redemption system while preserving item metadata such as enchantments, names, and lore.

---

## Features

1. **Opt-in System**: Players can opt into the Secret Santa event using a command.
2. **Recipient Assignment**: Each participant is randomly assigned a recipient.
3. **Gift Submission**: Players can submit gifts for their recipient while preserving all item metadata.
4. **Gift Redemption**: Players can redeem their gifts on or after a configurable reveal date.
---

## Configuration

### `config.yml`
```yaml
reveal-date: "2024-12-25T00:00:00" # ISO 8601 format for the reveal date
redemption-start: "2024-12-25T00:00:00"
redemption-end: "2025-01-01T00:00:00"
```

## Commands
* /kc join 
  * Description: Opt into the Secret Santa event.
* /kc reveal
  Description: Reveal your assigned recipient (after the reveal date).
* /kc submit
  Description: Submit a gift for your assigned recipient.
  /kc redeem
  Description: Redeem gifts from your Secret Santa.
  Permissions
  kringlecrate.override: Allows admins to bypass reveal date restrictions.

## How It Works
* Players use `/kc join` to join the event. 
* The plugin assigns a random recipient to each participant. 
* Participants can submit a gift using `/kc submit` (before the redemption period begins). 
* On the reveal date, players can use `/kc reveal` to see their assigned recipient. 
* Gifts can be redeemed using `/kc redeem` during the redemption period.