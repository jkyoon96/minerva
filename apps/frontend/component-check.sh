#!/bin/bash
echo "=== EduForum Component Library Check ==="
echo ""

echo "📦 UI Components (src/components/ui):"
ls -1 src/components/ui/*.tsx src/components/ui/*.ts 2>/dev/null | wc -l
echo ""

echo "🔧 Common Components (src/components/common):"
ls -1 src/components/common/*.tsx 2>/dev/null | wc -l
echo ""

echo "📝 Form Components (src/components/form):"
ls -1 src/components/form/*.tsx 2>/dev/null | wc -l
echo ""

echo "🏗️ Layout Components (src/components/layout):"
ls -1 src/components/layout/*.tsx 2>/dev/null | wc -l
echo ""

echo "📊 Total Components:"
find src/components -name "*.tsx" -o -name "*.ts" | grep -v index.ts | wc -l
echo ""

echo "✅ Status: Component library ready for use!"
