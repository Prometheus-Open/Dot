# Contributing to Dot

Thank you for your interest in contributing to **Dot**!

## Getting Started

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/Prometheus-Open/Dot.git
   ```
3. Open `d:\Practical\Android\ClockMusicApp` (or the repo root) in **Android Studio** (Hedgehog or later)
4. Create a feature branch:
   ```bash
   git checkout -b feat/your-feature-name
   ```

## Code Style & Architecture

- **Kotlin + Jetpack Compose** (Material 3)
- **Min SDK:** 34 (Android 14) · **Target SDK:** 36 (Android 16)
- Follow the existing **Blockit × Nothing** design tokens in `ui/theme/Color.kt` and `ui/theme/Type.kt`
- Keep layouts adaptive for both **Tablet (≥600dp)** and **Phone**

## Pull Requests

- Keep PRs focused and well-described
- Include screenshots if you modify any UI
- Ensure the project builds cleanly without warnings
