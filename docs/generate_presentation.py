#!/usr/bin/env python3
"""Genere la presentation PowerPoint de FitTrack (python-pptx)."""

from pptx import Presentation
from pptx.dml.color import RGBColor
from pptx.enum.shapes import MSO_SHAPE
from pptx.enum.text import MSO_ANCHOR, PP_ALIGN
from pptx.util import Emu, Inches, Pt

DARK = RGBColor(0x08, 0x0E, 0x1A)
PANEL = RGBColor(0x11, 0x1C, 0x2E)
PANEL_2 = RGBColor(0x15, 0x23, 0x39)
LINE = RGBColor(0x25, 0x36, 0x50)
ACCENT = RGBColor(0xC6, 0xFF, 0x4A)
BLUE = RGBColor(0x57, 0xC9, 0xF5)
WHITE = RGBColor(0xF2, 0xF7, 0xFF)
MUTED = RGBColor(0x8E, 0xA3, 0xC0)

OUT = "FitTrack-Presentation.pptx"

prs = Presentation()
prs.slide_width = Inches(13.333)
prs.slide_height = Inches(7.5)
BLANK = prs.slide_layouts[6]

SLIDE_W = prs.slide_width
SLIDE_H = prs.slide_height


def new_slide():
    slide = prs.slides.add_slide(BLANK)
    background = slide.background
    background.fill.solid()
    background.fill.fore_color.rgb = DARK
    # bande decorative discrete
    band = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, 0, 0, Inches(0.12), SLIDE_H)
    band.fill.solid()
    band.fill.fore_color.rgb = ACCENT
    band.line.fill.background()
    return slide


def textbox(slide, left, top, width, height, text, size=18, bold=False,
            color=WHITE, align=PP_ALIGN.LEFT, font="Arial", line_spacing=1.15,
            space_after=0):
    box = slide.shapes.add_textbox(left, top, width, height)
    frame = box.text_frame
    frame.word_wrap = True
    frame.margin_left = 0
    frame.margin_right = 0
    frame.margin_top = 0
    frame.margin_bottom = 0
    lines = text.split("\n")
    for index, line in enumerate(lines):
        paragraph = frame.paragraphs[0] if index == 0 else frame.add_paragraph()
        paragraph.text = line
        paragraph.alignment = align
        paragraph.line_spacing = line_spacing
        paragraph.space_after = Pt(space_after)
        run = paragraph.runs[0]
        run.font.size = Pt(size)
        run.font.bold = bold
        run.font.color.rgb = color
        run.font.name = font
    return box


def eyebrow(slide, text, top=Inches(0.55)):
    textbox(slide, Inches(0.85), top, Inches(11.8), Inches(0.3), text.upper(),
            size=11, bold=True, color=ACCENT)


def title(slide, text, top=Inches(0.9), size=34):
    textbox(slide, Inches(0.85), top, Inches(11.6), Inches(1.1), text,
            size=size, bold=True, color=WHITE)


def subtitle(slide, text, top=Inches(1.95), width=Inches(10.6)):
    textbox(slide, Inches(0.85), top, width, Inches(0.8), text, size=14,
            color=MUTED)


def accent_line(slide, top=Inches(1.72), width=Inches(1.4)):
    line = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.85), top,
                                  width, Inches(0.05))
    line.fill.solid()
    line.fill.fore_color.rgb = ACCENT
    line.line.fill.background()


def footer(slide, index):
    textbox(slide, Inches(0.85), Inches(7.02), Inches(5), Inches(0.3),
            "FitTrack · Application de fitness tracking", size=9, color=MUTED)
    textbox(slide, Inches(11.9), Inches(7.02), Inches(0.8), Inches(0.3),
            "%02d" % index, size=9, color=MUTED, align=PP_ALIGN.RIGHT)


def card(slide, left, top, width, height, heading, body, tone="default",
         heading_size=15, body_size=11.5):
    shape = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top,
                                   width, height)
    shape.adjustments[0] = 0.06
    shape.fill.solid()
    shape.fill.fore_color.rgb = PANEL
    shape.line.color.rgb = LINE
    shape.line.width = Pt(1)
    shape.shadow.inherit = False
    pad = Inches(0.22)
    heading_color = ACCENT if tone == "accent" else WHITE
    box = slide.shapes.add_textbox(left + pad, top + Inches(0.18),
                                   width - 2 * pad, height - Inches(0.3))
    frame = box.text_frame
    frame.word_wrap = True
    frame.margin_left = 0
    frame.margin_right = 0
    frame.margin_top = 0
    frame.margin_bottom = 0
    first = frame.paragraphs[0]
    first.text = heading
    first.space_after = Pt(6)
    first.font.size = Pt(heading_size)
    first.font.bold = True
    first.font.color.rgb = heading_color
    first.font.name = "Arial"
    second = frame.add_paragraph()
    second.text = body
    second.font.size = Pt(body_size)
    second.font.color.rgb = MUTED
    second.font.name = "Arial"
    second.line_spacing = 1.2
    return shape


def stat(slide, left, top, width, height, value, label):
    shape = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top,
                                   width, height)
    shape.adjustments[0] = 0.08
    shape.fill.solid()
    shape.fill.fore_color.rgb = PANEL_2
    shape.line.color.rgb = LINE
    shape.line.width = Pt(0.75)
    shape.shadow.inherit = False
    textbox(slide, left + Inches(0.22), top + Inches(0.16), width - Inches(0.4),
            Inches(0.5), value, size=26, bold=True, color=ACCENT)
    textbox(slide, left + Inches(0.22), top + Inches(0.62), width - Inches(0.4),
            Inches(0.4), label, size=9.5, color=MUTED)


def bullet_list(slide, left, top, width, items, size=14, gap=0.5):
    y = top
    for item in items:
        dot = slide.shapes.add_shape(MSO_SHAPE.OVAL, left, y + Inches(0.09),
                                     Inches(0.09), Inches(0.09))
        dot.fill.solid()
        dot.fill.fore_color.rgb = ACCENT
        dot.line.fill.background()
        box = slide.shapes.add_textbox(left + Inches(0.28), y,
                                       width - Inches(0.28), Inches(gap))
        frame = box.text_frame
        frame.word_wrap = True
        frame.margin_top = 0
        frame.margin_bottom = 0
        paragraph = frame.paragraphs[0]
        paragraph.text = item
        paragraph.font.size = Pt(size)
        paragraph.font.color.rgb = WHITE
        paragraph.font.name = "Arial"
        y += Inches(gap * 0.95) + Inches(0.18)


def pill(slide, left, top, text, width=Inches(1.9), color=ACCENT):
    shape = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top,
                                   width, Inches(0.36))
    shape.adjustments[0] = 0.5
    shape.fill.solid()
    shape.fill.fore_color.rgb = PANEL_2
    shape.line.color.rgb = color
    shape.line.width = Pt(0.75)
    shape.shadow.inherit = False
    frame = shape.text_frame
    frame.word_wrap = False
    paragraph = frame.paragraphs[0]
    paragraph.text = text
    paragraph.alignment = PP_ALIGN.CENTER
    paragraph.font.size = Pt(10)
    paragraph.font.color.rgb = color
    paragraph.font.name = "Arial"


# ------------------------------------------------------------------ 01 Titre
slide = new_slide()
glow = slide.shapes.add_shape(MSO_SHAPE.OVAL, Inches(9.4), Inches(-1.6),
                              Inches(6.2), Inches(6.2))
glow.fill.solid()
glow.fill.fore_color.rgb = RGBColor(0x12, 0x22, 0x33)
glow.line.fill.background()

textbox(slide, Inches(0.85), Inches(1.25), Inches(6), Inches(0.35),
        "FITNESS TRACKING", size=12, bold=True, color=ACCENT)
textbox(slide, Inches(0.85), Inches(1.7), Inches(8.4), Inches(1.6),
        "FitTrack", size=76, bold=True, color=WHITE)
line = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.9), Inches(3.35),
                              Inches(1.6), Inches(0.07))
line.fill.solid()
line.fill.fore_color.rgb = ACCENT
line.line.fill.background()
textbox(slide, Inches(0.85), Inches(3.6), Inches(8.6), Inches(1.0),
        "Transformez vos seances en progression mesurable",
        size=25, color=WHITE)
textbox(slide, Inches(0.85), Inches(4.5), Inches(7.6), Inches(1.0),
        "Séances détaillées, calories, mesures corporelles et tableau de bord analytique.\n"
        "Java 17 · Spring Boot 3.2 · Spring Security · Thymeleaf · H2 / MySQL",
        size=12.5, color=MUTED, line_spacing=1.35)
textbox(slide, Inches(0.85), Inches(6.7), Inches(8), Inches(0.3),
        "Version 1.0 · Novembre 2026 · Documentation et code source inclus",
        size=10, color=MUTED)
for index, height in enumerate([0.9, 1.5, 0.6, 1.9, 1.2]):
    bar = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE,
                                 Inches(10.35 + index * 0.48),
                                 Inches(5.3 - height), Inches(0.3),
                                 Inches(height))
    bar.adjustments[0] = 0.25
    bar.fill.solid()
    bar.fill.fore_color.rgb = ACCENT if index == 3 else RGBColor(0x28, 0x3D, 0x59)
    bar.line.fill.background()

# ------------------------------------------------------------- 02 Probleme
slide = new_slide()
eyebrow(slide, "01 · Le constat")
title(slide, "Le suivi sportif est fragmenté")
subtitle(slide, "Les données existent, mais elles ne racontent aucune histoire.",
         width=Inches(9.5))
bullet_list(slide, Inches(0.85), Inches(2.55), Inches(6.1), [
    "Une application pour courir, un carnet pour la musculation, une note pour le poids.",
    "Aucun historique exploitable : impossible de comparer deux semaines.",
    "Les charges, séries et durées ne sont pas enregistrées.",
    "Aucun lien entre l'entraînement et l'évolution corporelle.",
    "Les outils existants sont complexes ou payants pour un usage personnel.",
], size=13.5, gap=0.52)
stat(slide, Inches(7.45), Inches(2.45), Inches(2.3), Inches(1.25), "5", "sources dispersées")
stat(slide, Inches(10.0), Inches(2.45), Inches(2.4), Inches(1.25), "0", "vision d'ensemble")
stat(slide, Inches(7.45), Inches(3.95), Inches(2.3), Inches(1.25), "1 à 2 kg", "variation de poids ignorée")
stat(slide, Inches(10.0), Inches(3.95), Inches(2.4), Inches(1.25), "40", "séances non mesurées")
textbox(slide, Inches(7.45), Inches(5.55), Inches(4.95), Inches(0.9),
        "Sans données structurées, la progression repose sur des impressions.",
        size=12.5, color=ACCENT, line_spacing=1.3)
footer(slide, 2)

# ------------------------------------------------------------- 03 Solution
slide = new_slide()
eyebrow(slide, "02 · La solution")
title(slide, "FitTrack centralise tout le suivi")
subtitle(slide, "Une application web qui enregistre, calcule et restitue la progression.",
         width=Inches(10.4))
card(slide, Inches(0.85), Inches(2.55), Inches(3.7), Inches(2.4),
     "Enregistrer", "Séances datées, type, durée, intensité, exercices, séries, "
     "charges, distance et notes.", heading_size=18, body_size=12)
card(slide, Inches(4.8), Inches(2.55), Inches(3.7), Inches(2.4),
     "Calculer", "Calories (MET), volume soulevé, IMC, métabolisme de base, "
     "série de semaines et records.", tone="accent", heading_size=18, body_size=12)
card(slide, Inches(8.75), Inches(2.55), Inches(3.7), Inches(2.4),
     "Progresser", "Tableau de bord, graphiques 7 jours et 8 semaines, courbe de "
     "poids, objectifs et tendances.", heading_size=18, body_size=12)
textbox(slide, Inches(0.85), Inches(5.45), Inches(11.6), Inches(0.9),
        "Une seule application, un seul modèle de données, une vision complète de "
        "l'entraînement et de la composition corporelle.",
        size=14, color=MUTED, line_spacing=1.35)
pills = ["Java 17", "Spring Boot 3.2", "Thymeleaf", "Spring Security", "H2 / MySQL"]
x = Inches(0.85)
for label in pills:
    pill(slide, x, Inches(6.25), label, width=Inches(2.1))
    x += Inches(2.25)
footer(slide, 3)

# ---------------------------------------------------------- 04 Fonctionnalites
slide = new_slide()
eyebrow(slide, "03 · Fonctionnalités")
title(slide, "Cinq espaces, un parcours fluide")
subtitle(slide, "Chaque écran répond à une question concrète de l'utilisateur.",
         width=Inches(10))
features = [
    ("Tableau de bord", "4 KPI, objectif hebdomadaire, graphiques et records"),
    ("Séances", "Création, édition, filtres, exercices et volume soulevé"),
    ("Exercices", "32 exercices, groupes musculaires, MET, ajout personnel"),
    ("Mesures", "Poids, masse grasse, mensurations, IMC et courbe"),
    ("Profil", "Taille, âge, activité, objectif de séances, MB et TDEE"),
    ("API REST", "Statistiques JSON pour une future application mobile"),
]
positions = [(0.85, 2.5), (4.85, 2.5), (8.85, 2.5), (0.85, 4.35), (4.85, 4.35), (8.85, 4.35)]
for (label, body), (x, y) in zip(features, positions):
    card(slide, Inches(x), Inches(y), Inches(3.6), Inches(1.6), label, body,
         heading_size=14, body_size=11)
textbox(slide, Inches(0.85), Inches(6.3), Inches(11.6), Inches(0.5),
        "Données de démonstration : 10 semaines d'historique, 40 séances, 10 mesures.",
        size=11, color=ACCENT)
footer(slide, 4)

# ------------------------------------------------------------- 05 Parcours
slide = new_slide()
eyebrow(slide, "04 · Parcours utilisateur")
title(slide, "De l'inscription à l'analyse")
subtitle(slide, "Un rituel de suivi en moins d'une minute par séance.", width=Inches(9))
steps = [
    ("01", "Créer son compte", "Formulaire validé, mot de passe BCrypt, connexion immédiate."),
    ("02", "Définir son profil", "Taille, âge, niveau d'activité et objectif hebdomadaire."),
    ("03", "Enregistrer une séance", "Type, durée, intensité et notes, puis exercices réalisés."),
    ("04", "Mesurer et ajuster", "Poids et mensurations, puis lecture des tendances."),
]
x = Inches(0.85)
for number, heading, body in steps:
    textbox(slide, x, Inches(2.5), Inches(2.6), Inches(0.6), number,
            size=30, bold=True, color=ACCENT)
    textbox(slide, x, Inches(3.2), Inches(2.5), Inches(0.5), heading,
            size=14, bold=True, color=WHITE)
    textbox(slide, x, Inches(3.65), Inches(2.45), Inches(1.6), body,
            size=11, color=MUTED, line_spacing=1.25)
    if number != "04":
        arrow = slide.shapes.add_shape(MSO_SHAPE.RIGHT_ARROW, x + Inches(2.52),
                                       Inches(2.68), Inches(0.42), Inches(0.22))
        arrow.fill.solid()
        arrow.fill.fore_color.rgb = LINE
        arrow.line.fill.background()
    x += Inches(3.05)
card(slide, Inches(0.85), Inches(5.55), Inches(11.6), Inches(1.0),
     "Boucle de progrès",
     "Chaque séance enregistrée alimente le tableau de bord, les records et la série de semaines actives.",
     tone="accent", heading_size=13, body_size=11)
footer(slide, 5)

# ----------------------------------------------------------- 06 Architecture
slide = new_slide()
eyebrow(slide, "05 · Architecture")
title(slide, "Une architecture en couches, testable")
subtitle(slide, "Spring Boot 3.2, MVC côté serveur, aucune logique métier dans les vues.",
         width=Inches(11))
layers = [
    ("Navigateur", "HTML / CSS · Thymeleaf · graphiques CSS et SVG", PANEL_2, WHITE),
    ("Spring Security", "Session · BCrypt · CSRF · autorisation par ressource", PANEL_2, ACCENT),
    ("Web", "Contrôleurs MVC, API REST JSON, DTO validés", RGBColor(0x1B, 0x33, 0x50), WHITE),
    ("Service", "Règles métier, calculs, agrégations, transactions", RGBColor(0x24, 0x47, 0x66), WHITE),
    ("Repository", "Spring Data JPA · Hibernate", RGBColor(0x2E, 0x5B, 0x75), WHITE),
    ("Base", "H2 embarqué (défaut) · MySQL 8 (profil)", RGBColor(0x3A, 0x71, 0x83), WHITE),
]
y = Inches(2.35)
for label, detail, color, fg in layers:
    shape = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.85), y,
                                   Inches(7.6), Inches(0.66))
    shape.adjustments[0] = 0.12
    shape.fill.solid()
    shape.fill.fore_color.rgb = color
    shape.line.color.rgb = LINE
    shape.line.width = Pt(0.5)
    frame = shape.text_frame
    frame.margin_left = Inches(0.2)
    frame.margin_top = 0
    frame.margin_bottom = 0
    paragraph = frame.paragraphs[0]
    paragraph.text = label
    paragraph.font.size = Pt(12.5)
    paragraph.font.bold = True
    paragraph.font.color.rgb = fg
    paragraph.font.name = "Arial"
    textbox(slide, Inches(8.65), y + Inches(0.18), Inches(3.9), Inches(0.4),
            detail, size=10, color=MUTED)
    y += Inches(0.76)
textbox(slide, Inches(0.85), Inches(6.95), Inches(11.6), Inches(0.3),
        "Code organisé en config / domain / repository / service / web.",
        size=10, color=MUTED)
footer(slide, 6)

# ---------------------------------------------------------- 07 Modele donnees
slide = new_slide()
eyebrow(slide, "06 · Données")
title(slide, "Un modèle relationnel centré sur l'athlète")
subtitle(slide, "Six tables principales, des relations simples et isolées par utilisateur.",
         width=Inches(10.5))
entities = [
    ("users", "compte, profil, objectifs", 0.85, 2.45),
    ("workouts", "séance datée, type, durée", 4.85, 2.45),
    ("workout_exercises", "séries, reps, charge", 8.85, 2.45),
    ("exercises", "bibliothèque, MET", 0.85, 4.15),
    ("measurements", "poids, IMC, mensurations", 4.85, 4.15),
    ("roles", "ROLE_USER / ROLE_ADMIN", 8.85, 4.15),
]
for name, detail, x, y in entities:
    card(slide, Inches(x), Inches(y), Inches(3.6), Inches(1.35),
         name, detail, heading_size=14, body_size=11)
card(slide, Inches(0.85), Inches(5.85), Inches(11.6), Inches(0.95),
     "Règles clés",
     "1 utilisateur → n séances → n lignes d'exercices ; 1 exercice → n lignes ; "
     "données filtrées systématiquement par utilisateur authentifié.",
     heading_size=12, body_size=11)
footer(slide, 7)

# ------------------------------------------------------------ 08 Calculs
slide = new_slide()
eyebrow(slide, "07 · Algorithmes")
title(slide, "Des indicateurs explicables et testés")
subtitle(slide, "Les calculs sont centralisés dans FitnessCalculator et couverts par des tests unitaires.",
         width=Inches(11))
calculs = [
    ("Calories", "MET × intensité × 3,5 × poids / 200 × minutes"),
    ("Volume", "Σ (séries × répétitions × charge)"),
    ("IMC", "poids / taille(m)²"),
    ("Métabolisme de base", "10×poids + 6,25×taille − 5×âge ± constante"),
    ("Dépense totale", "MB × facteur d'activité (1,2 à 1,725)"),
    ("Série", "semaines consécutives avec ≥ 1 séance"),
]
positions = [(0.85, 2.4), (4.85, 2.4), (8.85, 2.4), (0.85, 4.0), (4.85, 4.0), (8.85, 4.0)]
for (label, formula), (x, y) in zip(calculs, positions):
    card(slide, Inches(x), Inches(y), Inches(3.6), Inches(1.35), label, formula,
         heading_size=13, body_size=11)
textbox(slide, Inches(0.85), Inches(5.62), Inches(11.6), Inches(0.8),
        "Exemple : musculation 60 min, intensité modérée, 80 kg → 5,0 × 1,0 × 3,5 × 80 / 200 × 60 = 420 kcal.",
        size=12, color=ACCENT, line_spacing=1.3)
textbox(slide, Inches(0.85), Inches(6.15), Inches(11.6), Inches(0.6),
        "Les graphiques sont générés côté serveur (CSS / SVG) : aucune dépendance JavaScript.",
        size=11, color=MUTED)
footer(slide, 8)

# ---------------------------------------------------------- 09 Tableau de bord
slide = new_slide()
eyebrow(slide, "08 · Analyse")
title(slide, "Un tableau de bord qui décide")
subtitle(slide, "Les chiffres de la semaine, la tendance du mois et les records personnels.",
         width=Inches(10.5))
stat(slide, Inches(0.85), Inches(2.45), Inches(2.55), Inches(1.3), "4 / 4", "objectif hebdomadaire")
stat(slide, Inches(3.7), Inches(2.45), Inches(2.55), Inches(1.3), "225 min", "cette semaine")
stat(slide, Inches(6.55), Inches(2.45), Inches(2.55), Inches(1.3), "2 197", "kcal estimées")
stat(slide, Inches(9.4), Inches(2.45), Inches(2.9), Inches(1.3), "10", "semaines consécutives")
bullet_list(slide, Inches(0.85), Inches(4.15), Inches(5.6), [
    "Barres des 7 derniers jours avec le jour courant mis en évidence.",
    "Historique 8 semaines pour repérer les creux.",
    "Courbe de poids et écart avec l'objectif.",
], size=12.5, gap=0.5)
bullet_list(slide, Inches(7.0), Inches(4.15), Inches(5.4), [
    "Records : charge maximale et séance la plus longue.",
    "Dernières séances cliquables.",
    "Progression de l'objectif et total cumulé.",
], size=12.5, gap=0.5)
footer(slide, 9)

# ------------------------------------------------------------ 10 Securite
slide = new_slide()
eyebrow(slide, "09 · Sécurité")
title(slide, "Comptes protégés, données isolées")
subtitle(slide, "Spring Security 6, BCrypt et autorisation systématique par ressource.",
         width=Inches(10.5))
card(slide, Inches(0.85), Inches(2.5), Inches(3.6), Inches(1.75),
     "Authentification", "Formulaire personnalisé, BCrypt, messages neutres, déconnexion POST.",
     heading_size=14, body_size=11)
card(slide, Inches(4.85), Inches(2.5), Inches(3.6), Inches(1.75),
     "CSRF", "Jeton injecté automatiquement dans chaque formulaire Thymeleaf.",
     tone="accent", heading_size=14, body_size=11)
card(slide, Inches(8.85), Inches(2.5), Inches(3.6), Inches(1.75),
     "Isolation", "findByIdAndUser : aucun accès aux données d'un autre compte.",
     heading_size=14, body_size=11)
card(slide, Inches(0.85), Inches(4.5), Inches(3.6), Inches(1.5),
     "Validation", "Bornes explicites sur tous les formulaires (Jakarta Validation).",
     heading_size=14, body_size=11)
card(slide, Inches(4.85), Inches(4.5), Inches(3.6), Inches(1.5),
     "Rôles", "ROLE_USER / ROLE_ADMIN, pages publiques limitées.",
     heading_size=14, body_size=11)
card(slide, Inches(8.85), Inches(4.5), Inches(3.6), Inches(1.5),
     "En-têtes", "X-Frame-Options SAMEORIGIN, console H2 désactivée en production.",
     heading_size=14, body_size=11)
footer(slide, 10)

# ------------------------------------------------------------- 11 Tests
slide = new_slide()
eyebrow(slide, "10 · Qualité")
title(slide, "12 tests automatisés, tous verts")
subtitle(slide, "Calculs métier et parcours web sensibles vérifiés à chaque build.",
         width=Inches(10))
stat(slide, Inches(0.85), Inches(2.5), Inches(3.5), Inches(1.5), "7", "tests unitaires de calculs")
stat(slide, Inches(4.75), Inches(2.5), Inches(3.5), Inches(1.5), "5", "tests d'intégration web")
stat(slide, Inches(8.65), Inches(2.5), Inches(3.7), Inches(1.5), "BUILD SUCCESS", "mvn test")
bullet_list(slide, Inches(0.85), Inches(4.35), Inches(5.6), [
    "Formule calorique, intensité, IMC, métabolisme, série.",
    "Contexte Spring et jeu de données de démonstration.",
    "Redirection des pages protégées.",
], size=12.5, gap=0.5)
bullet_list(slide, Inches(7.0), Inches(4.35), Inches(5.4), [
    "Rendu du tableau de bord authentifié.",
    "Réponse JSON de l'API.",
    "Vérification manuelle des parcours d'écriture.",
], size=12.5, gap=0.5)
footer(slide, 11)

# --------------------------------------------------------------- 12 Stack
slide = new_slide()
eyebrow(slide, "11 · Stack")
title(slide, "Une base technique solide")
subtitle(slide, "Des choix standards, documentés et faciles à reprendre.", width=Inches(9.5))
stack = [
    ("Langage", "Java 17"),
    ("Framework", "Spring Boot 3.2.5"),
    ("Web", "Spring MVC · Thymeleaf 3"),
    ("Sécurité", "Spring Security 6"),
    ("Données", "Spring Data JPA · Hibernate"),
    ("Base", "H2 (défaut) · MySQL 8"),
    ("Validation", "Jakarta Bean Validation"),
    ("Tests", "JUnit 5 · MockMvc · AssertJ"),
    ("Build", "Maven 3.9"),
]
x, y = 0.85, 2.5
for index, (label, value) in enumerate(stack):
    card(slide, Inches(x), Inches(y), Inches(3.6), Inches(1.15),
         label, value, heading_size=12, body_size=11.5)
    x += 4.0
    if index % 3 == 2:
        x = 0.85
        y += 1.35
textbox(slide, Inches(0.85), Inches(6.55), Inches(11.6), Inches(0.4),
        "Interface sans dépendance front externe : CSS et SVG générés côté serveur.",
        size=11, color=ACCENT)
footer(slide, 12)

# ---------------------------------------------------------------- 13 Demo
slide = new_slide()
eyebrow(slide, "12 · Démonstration")
title(slide, "À tester maintenant")
subtitle(slide, "Application lancée localement, données de démonstration prêtes.",
         width=Inches(10))
card(slide, Inches(0.85), Inches(2.5), Inches(5.4), Inches(2.15),
     "Compte de démonstration",
     "demo@fittrack.app\nMot de passe : Demo1234!\n\n10 semaines d'historique · 40 séances · 10 mesures",
     tone="accent", heading_size=16, body_size=13)
card(slide, Inches(6.65), Inches(2.5), Inches(5.8), Inches(2.15),
     "Scénario conseillé",
     "1. Tableau de bord et objectif\n2. Détail d'une séance et volume\n3. Ajout d'un exercice\n"
     "4. Mesures et courbe de poids\n5. API /api/stats/summary",
     heading_size=16, body_size=12)
textbox(slide, Inches(0.85), Inches(5.05), Inches(11.6), Inches(0.5),
        "http://localhost:8080", size=22, bold=True, color=ACCENT)
textbox(slide, Inches(0.85), Inches(5.6), Inches(11.6), Inches(0.7),
        "Démarrage : mvn spring-boot:run    ·    Tests : mvn test    ·    JAR : java -jar target/fittrack-1.0.0.jar",
        size=11, color=MUTED)
footer(slide, 13)

# --------------------------------------------------------------- 14 Roadmap
slide = new_slide()
eyebrow(slide, "13 · Perspectives")
title(slide, "Feuille de route")
subtitle(slide, "Prolonger le produit sans remettre en cause l'architecture.", width=Inches(9.5))
card(slide, Inches(0.85), Inches(2.5), Inches(3.7), Inches(2.9),
     "Court terme",
     "API REST complète (CRUD)\nExport CSV\nPagination et index\nThème clair\nObjectifs multiples",
     heading_size=16, body_size=12)
card(slide, Inches(4.8), Inches(2.5), Inches(3.7), Inches(2.9),
     "Moyen terme",
     "Application mobile\nProgrammes planifiés\nRappels et notifications\nImport montres connectées\nStatistiques avancées",
     tone="accent", heading_size=16, body_size=12)
card(slide, Inches(8.75), Inches(2.5), Inches(3.7), Inches(2.9),
     "Long terme",
     "Recommandations personnalisées\nCoaching et objectifs adaptatifs\nMulti-langue\nDéploiement cloud\nAdministration",
     heading_size=16, body_size=12)
textbox(slide, Inches(0.85), Inches(5.75), Inches(11.6), Inches(0.6),
        "Chaque évolution s'appuie sur les couches service et repository existantes.",
        size=12, color=MUTED)
footer(slide, 14)

# ------------------------------------------------------------- 15 Conclusion
slide = new_slide()
eyebrow(slide, "14 · Conclusion")
title(slide, "Un projet complet, prêt à évoluer", size=38)
subtitle(slide, "FitTrack démontre une chaîne complète : conception, développement, sécurité, tests et documentation.",
         width=Inches(10.8))
values = [
    ("Conception", "Modèle de données cohérent, règles métier explicites"),
    ("Développement", "Spring Boot, Thymeleaf, service en couches"),
    ("Qualité", "12 tests, validation, pages d'erreur"),
    ("Livraison", "README, PDF, présentation, archive du projet"),
]
x = Inches(0.85)
for label, body in values:
    card(slide, x, Inches(2.65), Inches(2.85), Inches(1.7), label, body,
         heading_size=13, body_size=10.5)
    x += Inches(3.0)
textbox(slide, Inches(0.85), Inches(4.95), Inches(11.6), Inches(0.9),
        "Merci de votre attention.",
        size=34, bold=True, color=WHITE)
textbox(slide, Inches(0.85), Inches(5.85), Inches(11.6), Inches(0.6),
        "Questions, remarques et pistes d'amélioration bienvenues.",
        size=14, color=ACCENT)
textbox(slide, Inches(0.85), Inches(6.75), Inches(11.6), Inches(0.3),
        "FitTrack · Version 1.0 · Novembre 2026",
        size=10, color=MUTED)

prs.save(OUT)
print("Presentation generee :", OUT, "-", len(prs.slides.__iter__.__self__._sldIdLst), "slides")
