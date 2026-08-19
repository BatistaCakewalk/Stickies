# Contributing to Stickies

Thanks for wanting to help. Stickies is a small project — keep that in mind.

## Before you start

Open an issue first if you're planning something big. No point spending hours on a PR that goes in a different direction than where the project is headed.

Small fixes (typos, obvious bugs) — just PR it, no need to ask.

## Branches

- `master` — stable, what ships
- `dev` — active development, base your work off this

PRs go to `dev`, not `master`.

## Getting set up

You'll need Java 21 and Gradle. Everything else is handled by the wrapper.

```
git clone https://github.com/BatistaCakewalk/Stickies.git
cd Stickies
./gradlew build
```

## Pull requests

- Branch off `dev`
- **Make sure it builds before opening the PR.** Seriously.
- Keep it focused — one thing per PR
- Write a decent description of what changed and why

## Code style

Match what's already there. The codebase uses:
- `LogService` for all logging — no `System.out.println` in production code
- Try-with-resources for all SQL statements
- Singleton pattern for `NoteManager` and `StorageHandler`

## License

By contributing, you agree your code falls under the project's [GPL-3.0 license](LICENSE).
