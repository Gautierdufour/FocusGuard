#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script de correction automatique pour StatisticsActivity.kt
Corrige les erreurs de compilation
"""

import os
import sys

def fix_file(filepath):
    """Corrige un fichier Kotlin"""
    print(f"📝 Traitement de {filepath}...")
    
    try:
        # Lire le fichier
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Correction 1: Icons.Filled.Brush → Icons.Filled.Edit
        count1 = content.count('Icons.Filled.Brush')
        content = content.replace('Icons.Filled.Brush', 'Icons.Filled.Edit')
        
        # Correction 2: .background(AppColors.GradientPrimary) → .background(brush = AppColors.GradientPrimary)
        count2 = content.count('.background(AppColors.GradientPrimary)')
        content = content.replace('.background(AppColors.GradientPrimary)', '.background(brush = AppColors.GradientPrimary)')
        
        # Vérifier si des changements ont été faits
        if content != original_content:
            # Créer une sauvegarde
            backup_path = filepath + '.backup'
            with open(backup_path, 'w', encoding='utf-8') as f:
                f.write(original_content)
            print(f"✅ Sauvegarde créée: {backup_path}")
            
            # Écrire le fichier corrigé
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            
            print(f"✅ Corrections appliquées:")
            print(f"   - Icons.Filled.Brush → Icons.Filled.Edit ({count1} occurrences)")
            print(f"   - .background(AppColors.GradientPrimary) → .background(brush = ...) ({count2} occurrences)")
            return True
        else:
            print("ℹ️  Aucune correction nécessaire - le fichier est déjà correct")
            return False
            
    except Exception as e:
        print(f"❌ Erreur: {e}")
        return False

def main():
    # Chemin du fichier à corriger
    base_path = r"C:\Users\gauti\AndroidStudioProjects\MyApplication2\app\src\main\java\com\example\myapplication"
    filepath = os.path.join(base_path, "StatisticsActivity.kt")
    
    print("=" * 60)
    print("🔧 CORRECTION AUTOMATIQUE DE StatisticsActivity.kt")
    print("=" * 60)
    print()
    
    if not os.path.exists(filepath):
        print(f"❌ Fichier introuvable: {filepath}")
        sys.exit(1)
    
    success = fix_file(filepath)
    
    print()
    print("=" * 60)
    if success:
        print("✅ CORRECTIONS TERMINÉES AVEC SUCCÈS !")
        print()
        print("Prochaines étapes:")
        print("1. Dans Android Studio: Build → Clean Project")
        print("2. Puis: Build → Rebuild Project")
    else:
        print("ℹ️  AUCUNE CORRECTION NÉCESSAIRE")
        print()
        print("Si l'erreur persiste, essayez:")
        print("1. File → Invalidate Caches → Invalidate and Restart")
    print("=" * 60)

if __name__ == "__main__":
    main()
