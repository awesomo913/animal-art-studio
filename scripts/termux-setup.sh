#!/data/data/com.termux/files/usr/bin/bash
#
# Animal Art Studio — Termux quickstart
#
# One-shot install + boot of the Ktor backend on your phone, so the
# `PawsAndDoodles` APK (v0.2.1+, built to talk to http://127.0.0.1:8080/)
# becomes fully self-contained — no PC, no LAN, no second device.
#
# Usage in Termux:
#   pkg install -y curl
#   curl -fsSL https://raw.githubusercontent.com/awesomo913/animal-art-studio/master/scripts/termux-setup.sh | bash
#
# Or if you already cloned the repo:
#   bash ~/animal-art-studio/scripts/termux-setup.sh
#
# Requires: F-Droid build of Termux (the Play Store version is stale and won't
# install openjdk-17).

set -euo pipefail

REPO_URL="https://github.com/awesomo913/animal-art-studio.git"
CLONE_DIR="$HOME/animal-art-studio"

echo "==> Termux setup: ensuring required packages..."
pkg update -y >/dev/null
pkg install -y openjdk-17 git

echo "==> JAVA_HOME = $(command -v java | xargs readlink -f | xargs dirname | xargs dirname)"
java -version

if [ -d "$CLONE_DIR/.git" ]; then
  echo "==> Existing clone at $CLONE_DIR — fetching latest..."
  git -C "$CLONE_DIR" pull --ff-only
else
  echo "==> Cloning $REPO_URL into $CLONE_DIR..."
  git clone --depth 1 "$REPO_URL" "$CLONE_DIR"
fi

cd "$CLONE_DIR/backend"

echo "==> First boot may take ~5 min: Gradle downloads (~150 MB) + Ktor deps (~50 MB)."
echo "==> Plug in to power, stay on WiFi. Subsequent boots take ~15 s."
echo "==> Server will listen on http://127.0.0.1:8080/ — open the APK after you see 'Responding at'."
echo
exec ./gradlew run --no-daemon --console=plain
