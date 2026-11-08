#!/usr/bin/env python3
"""Genere la documentation technique PDF de FitTrack (reportlab)."""

import re

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.pdfbase.pdfmetrics import stringWidth
from reportlab.platypus import (BaseDocTemplate, Frame, HRFlowable, KeepTogether,
                                NextPageTemplate, PageBreak, PageTemplate,
                                Paragraph as _Paragraph, Spacer, Table, TableStyle)
from reportlab.platypus.tableofcontents import TableOfContents

DARK = colors.HexColor("#0B1220")
INK = colors.HexColor("#101C2E")
ACCENT = colors.HexColor("#79B700")
ACCENT_SOFT = colors.HexColor("#EFF7DC")
BLUE = colors.HexColor("#2E7FA8")
MUTED = colors.HexColor("#5F6E86")
TEXT = colors.HexColor("#222F42")
LINE = colors.HexColor("#DCE3ED")
PANEL = colors.HexColor("#F5F8FC")

OUT = "FitTrack-Documentation.pdf"

styles = getSampleStyleSheet()

BODY = ParagraphStyle("Body", parent=styles["BodyText"], fontName="Helvetica",
                      fontSize=9.6, leading=15, textColor=TEXT, alignment=TA_JUSTIFY,
                      spaceAfter=6)
BODY_LEFT = ParagraphStyle("BodyLeft", parent=BODY, alignment=TA_LEFT)
SMALL = ParagraphStyle("Small", parent=BODY, fontSize=8.3, leading=12.2,
                       textColor=MUTED, alignment=TA_LEFT, spaceAfter=2)
CAPTION = ParagraphStyle("Caption", parent=SMALL, fontName="Helvetica-Oblique",
                         alignment=TA_CENTER, spaceBefore=3, spaceAfter=10)
H1 = ParagraphStyle("H1", parent=styles["Heading1"], fontName="Helvetica-Bold",
                    fontSize=19, leading=24, textColor=INK, spaceBefore=6,
                    spaceAfter=2)
H1_PLAIN = ParagraphStyle("H1Plain", parent=H1)
H2 = ParagraphStyle("H2", parent=styles["Heading2"], fontName="Helvetica-Bold",
                    fontSize=12, leading=16, textColor=DARK, spaceBefore=12,
                    spaceAfter=4)
H3 = ParagraphStyle("H3", parent=styles["Heading3"], fontName="Helvetica-Bold",
                    fontSize=10, leading=14, textColor=ACCENT, spaceBefore=8,
                    spaceAfter=2)
EYEBROW = ParagraphStyle("Eyebrow", parent=SMALL, fontName="Helvetica-Bold",
                         fontSize=7.6, textColor=ACCENT, spaceAfter=2)
COVER_TITLE = ParagraphStyle("CoverTitle", fontName="Helvetica-Bold", fontSize=64,
                             leading=64, textColor=colors.white, spaceAfter=0)
COVER_SUB = ParagraphStyle("CoverSub", fontName="Helvetica-Bold", fontSize=19,
                           leading=24, textColor=colors.HexColor("#C6FF4A"),
                           spaceAfter=10)
COVER_TEXT = ParagraphStyle("CoverText", fontName="Helvetica", fontSize=10.5,
                            leading=16, textColor=colors.HexColor("#9FB2CC"))

CELL_HEAD = ParagraphStyle("CellHead", fontName="Helvetica-Bold", fontSize=7.6,
                           leading=9.5, textColor=colors.white)
CELL = ParagraphStyle("Cell", fontName="Helvetica", fontSize=8.4, leading=11.4,
                      textColor=TEXT)
CELL_BOLD = ParagraphStyle("CellBold", parent=CELL, fontName="Helvetica-Bold")


ACCENTS = {
    "matieres": "matières", "problematique": "problématique",
    "Modele": "Modèle", "modele": "modèle", "donnees": "données",
    "donnee": "donnée", "Qualite": "Qualité", "qualite": "qualité",
    "Securite": "Sécurité", "securite": "sécurité", "securisee": "sécurisée",
    "securise": "sécurisé", "metier": "métier", "metiers": "métiers",
    "Prerequis": "Prérequis", "prerequis": "prérequis", "Demo": "Démo",
    "demonstration": "démonstration", "seance": "séance", "seances": "séances",
    "Seance": "Séance", "Seances": "Séances", "realise": "réalisé",
    "realises": "réalisés", "realisee": "réalisée", "realisees": "réalisées",
    "realiser": "réaliser", "calculees": "calculées", "calcules": "calculés",
    "calculee": "calculée", "genere": "généré", "generes": "générés",
    "generee": "générée", "Generation": "Génération", "generation": "génération",
    "Developpement": "Développement", "developpement": "développement",
    "complete": "complète", "complet": "complet", "activite": "activité",
    "activites": "activités", "Activite": "Activité", "identite": "identité",
    "dependance": "dépendance", "incomplete": "incomplète",
    "automatises": "automatisés", "automatise": "automatisé",
    "veritable": "véritable", "detaillees": "détaillées",
    "detaillee": "détaillée", "detail": "détail", "details": "détails",
    "reecriture": "réécriture", "presente": "présente",
    "presentent": "présentent", "presentation": "présentation",
    "Presentation": "Présentation", "quantifiees": "quantifiées",
    "synthetique": "synthétique", "demarrage": "démarrage",
    "embarque": "embarqué", "embarquee": "embarquée", "datees": "datées",
    "creee": "créée", "creation": "création", "Creation": "Création",
    "deconnexion": "déconnexion", "periode": "période",
    "Evolution": "Évolution", "evolution": "évolution",
    "controleurs": "contrôleurs", "delegue": "délègue", "regles": "règles",
    "accedent": "accèdent", "valides": "validés", "entites": "entités",
    "defaut": "défaut", "reutilise": "réutilisé", "rattaches": "rattachés",
    "apparaitre": "apparaître", "requetes": "requêtes",
    "ecriture": "écriture", "filtrees": "filtrées", "authentifie": "authentifié",
    "centralises": "centralisés", "testee": "testée", "resultats": "résultats",
    "Depense": "Dépense", "depense": "dépense",
    "Metabolisme": "Métabolisme", "metabolisme": "métabolisme",
    "consecutives": "consécutives", "energetique": "énergétique",
    "reelle": "réelle", "repetitions": "répétitions",
    "repetition": "répétition", "duree": "durée", "durees": "durées",
    "intensite": "intensité", "series": "séries", "serie": "série",
    "Serie": "Série", "adapte": "adapté", "etaient": "étaient",
    "empeche": "empêche", "dispersees": "dispersées",
    "necessaire": "nécessaire", "testes": "testés", "testees": "testées",
    "validee": "validée", "premiere": "première", "derniere": "dernière",
    "deja": "déjà", "tres": "très", "apres": "après", "meme": "même",
    "etre": "être", "elements": "éléments", "element": "élément",
    "specifique": "spécifique", "numero": "numéro", "ecran": "écran",
    "coherence": "cohérence", "coherent": "cohérent", "execute": "exécute",
    "execution": "exécution", "reponse": "réponse", "selection": "sélection",
    "edition": "édition", "precedent": "précédent",
    "precedente": "précédente", "different": "différent",
    "differente": "différente", "complement": "complément",
    "etapes": "étapes", "etape": "étape", "generale": "générale",
    "general": "général", "interet": "intérêt", "cree": "créé",
    "creer": "créer", "modifie": "modifié", "supprime": "supprimé",
    "definir": "définir", "defini": "défini", "definie": "définie",
    "equilibre": "équilibre", "frequence": "fréquence",
    "regularite": "régularité", "ameliorer": "améliorer",
    "amelioration": "amélioration", "evoluer": "évoluer",
    "priorite": "priorité", "eleve": "élevé", "repartition": "répartition",
    "simplifie": "simplifié", "simplifiee": "simplifiée",
    "verification": "vérification", "verifications": "vérifications",
    "verifie": "vérifié", "verifies": "vérifiés",
    "presentees": "présentées", "presentee": "présentée",
    "affichees": "affichées", "utilisees": "utilisées",
    "organisees": "organisées", "integre": "intégré", "integres": "intégrés",
    "integree": "intégrée", "detaille": "détaillé", "estimees": "estimées",
    "estime": "estimé", "consacre": "consacré", "consacree": "consacrée",
    "mesuree": "mesurée", "mesurees": "mesurées", "constatees": "constatées",
    "reel": "réel", "reelles": "réelles", "generaux": "généraux",
}

ACCENTS.update({
    "controle": "contrôle", "Controle": "Contrôle", "acces": "accès",
    "deploiement": "déploiement", "deployable": "déployable",
    "deployer": "déployer", "deploye": "déployé", "generees": "générées",
    "regle": "règle", "Regle": "Règle", "categorie": "catégorie",
    "necessite": "nécessite", "sante": "santé", "connectees": "connectées",
    "resume": "résumé", "Resume": "Résumé", "reste": "reste",
    "moderee": "modérée", "elevee": "élevée", "cote": "côté",
    "ecart": "écart", "legere": "légère", "completee": "complétée",
    "immediatement": "immédiatement", "differents": "différents",
    "regulier": "régulier", "reguliere": "régulière", "apercu": "aperçu",
    "calorique": "calorique", "securiser": "sécuriser",
    "moteur": "moteur", "testee": "testée",
})

_ACCENT_RE = {word: re.compile(r"(?<![\w-])" + re.escape(word) + r"(?![\w-])")
              for word in ACCENTS}


def fr(text):
    for word, replacement in ACCENTS.items():
        text = _ACCENT_RE[word].sub(replacement, text)
    return text


def esc(text):
    return fr(str(text)).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


class Paragraph(_Paragraph):
    """Paragraph qui applique automatiquement l'accentuation francaise."""

    def __init__(self, text, style=None, *args, **kwargs):
        if isinstance(text, str):
            text = fr(text)
        super().__init__(text, style, *args, **kwargs)


def p(text, style=BODY):
    return Paragraph(esc(text), style)


def rich(text, style=BODY):
    return Paragraph(text, style)


def bullets(items, style=BODY_LEFT):
    return [Paragraph("&bull; " + esc(item), style) for item in items]


def callout(title, text, tone="accent"):
    bg, bar = (ACCENT_SOFT, ACCENT) if tone == "accent" else (colors.HexColor("#EAF3FA"), BLUE)
    inner = [
        Paragraph(title, ParagraphStyle("CalloutTitle", fontName="Helvetica-Bold",
                                        fontSize=9.4, leading=12, textColor=DARK)),
        Spacer(1, 3),
        Paragraph(esc(text), ParagraphStyle("CalloutText", fontName="Helvetica",
                                            fontSize=8.9, leading=13.4, textColor=TEXT)),
    ]
    table = Table([[inner]], colWidths=[166 * mm])
    table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), bg),
        ("LINEBEFORE", (0, 0), (0, -1), 3, bar),
        ("LEFTPADDING", (0, 0), (-1, -1), 12),
        ("RIGHTPADDING", (0, 0), (-1, -1), 12),
        ("TOPPADDING", (0, 0), (-1, -1), 9),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 9),
    ]))
    return table


def section_title(number, title):
    return [
        Paragraph(f"<font color='#79B700'>{number}</font>", EYEBROW),
        Paragraph(esc(title), H1),
        HRFlowable(width="100%", thickness=1.1, color=LINE, spaceBefore=4, spaceAfter=10),
    ]


def data_table(header, rows, widths, align_center=None):
    data = [[Paragraph(esc(h), CELL_HEAD) for h in header]]
    for row in rows:
        data.append([Paragraph(esc(str(cell)), CELL) for cell in row])
    table = Table(data, colWidths=widths, repeatRows=1, hAlign="LEFT")
    style = [
        ("BACKGROUND", (0, 0), (-1, 0), DARK),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("GRID", (0, 0), (-1, -1), 0.4, LINE),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, PANEL]),
        ("LEFTPADDING", (0, 0), (-1, -1), 7),
        ("RIGHTPADDING", (0, 0), (-1, -1), 7),
        ("TOPPADDING", (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
    ]
    if align_center:
        for col in align_center:
            style.append(("ALIGN", (col, 0), (col, -1), "CENTER"))
    table.setStyle(TableStyle(style))
    return table


def card_box(title, lines, width=52 * mm):
    content = [Paragraph(esc(title), ParagraphStyle(
        "CardTitle", fontName="Helvetica-Bold", fontSize=8.6, leading=11,
        textColor=DARK)), Spacer(1, 3)]
    for line in lines:
        content.append(Paragraph(esc(line), ParagraphStyle(
            "CardLine", fontName="Helvetica", fontSize=7.8, leading=10.5,
            textColor=MUTED)))
    box = Table([[content]], colWidths=[width])
    box.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), PANEL),
        ("BOX", (0, 0), (-1, -1), 0.6, LINE),
        ("LEFTPADDING", (0, 0), (-1, -1), 8),
        ("RIGHTPADDING", (0, 0), (-1, -1), 8),
        ("TOPPADDING", (0, 0), (-1, -1), 8),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 8),
    ]))
    return box


class FitTrackDoc(BaseDocTemplate):
    def __init__(self, filename, **kwargs):
        super().__init__(filename, pagesize=A4, leftMargin=22 * mm,
                         rightMargin=22 * mm, topMargin=26 * mm,
                         bottomMargin=20 * mm,
                         title="FitTrack - Documentation technique",
                         author="FitTrack", subject="Documentation technique",
                         **kwargs)
        frame = Frame(self.leftMargin, self.bottomMargin,
                      self.width, self.height, id="content")
        cover_frame = Frame(22 * mm, 20 * mm, self.width, self.height, id="cover")
        self.addPageTemplates([
            PageTemplate(id="cover", frames=[cover_frame], onPage=self.cover_page),
            PageTemplate(id="content", frames=[frame], onPage=self.content_page),
        ])

    def cover_page(self, canvas, doc):
        width, height = A4
        canvas.saveState()
        canvas.setFillColor(DARK)
        canvas.rect(0, 0, width, height, fill=1, stroke=0)
        canvas.setFillColor(colors.HexColor("#122036"))
        canvas.rect(width - 78 * mm, 0, 78 * mm, height, fill=1, stroke=0)
        canvas.setFillColor(colors.HexColor("#C6FF4A"))
        canvas.rect(22 * mm, height - 78 * mm, 26 * mm, 3.2 * mm, fill=1, stroke=0)

        # miniature "dashboard" abstraite
        base_x = width - 66 * mm
        base_y = 38 * mm
        heights = [22, 40, 16, 52, 34, 60, 28]
        canvas.setFillColor(colors.HexColor("#1B2B44"))
        canvas.roundRect(base_x - 4 * mm, base_y - 6 * mm, 50 * mm, 72 * mm, 4 * mm,
                         fill=1, stroke=0)
        for index, bar in enumerate(heights):
            canvas.setFillColor(colors.HexColor("#C6FF4A") if index == 5
                                else colors.HexColor("#2F4568"))
            canvas.roundRect(base_x + index * 6.2 * mm, base_y, 4.2 * mm,
                             bar * 0.9, 1.2 * mm, fill=1, stroke=0)
        canvas.setFillColor(colors.HexColor("#7C90AC"))
        canvas.setFont("Helvetica", 7.5)
        canvas.drawString(base_x, base_y - 7 * mm, "APERÇU DU TABLEAU DE BORD")

        canvas.setFillColor(colors.HexColor("#C6FF4A"))
        canvas.setFont("Helvetica-Bold", 9)
        canvas.drawString(22 * mm, height - 64 * mm, "FITNESS TRACKING")
        canvas.setFillColor(colors.white)
        canvas.setFont("Helvetica-Bold", 52)
        canvas.drawString(22 * mm, height - 100 * mm, "FitTrack")
        canvas.setFillColor(colors.HexColor("#C6FF4A"))
        canvas.setFont("Helvetica-Bold", 16)
        canvas.drawString(22 * mm, height - 112 * mm,
                          "Application de suivi d'entraînement")
        canvas.setFillColor(colors.HexColor("#9FB2CC"))
        canvas.setFont("Helvetica", 10)
        canvas.drawString(22 * mm, height - 124 * mm,
                          "Documentation technique : conception, architecture")
        canvas.drawString(22 * mm, height - 131 * mm, "et guide utilisateur")
        canvas.setStrokeColor(colors.HexColor("#26374F"))
        canvas.setLineWidth(0.8)
        canvas.line(22 * mm, 70 * mm, 116 * mm, 70 * mm)
        canvas.setFillColor(colors.HexColor("#7C90AC"))
        canvas.setFont("Helvetica", 8.5)
        canvas.drawString(22 * mm, 60 * mm, "Version 1.0  -  Novembre 2026")
        canvas.drawString(22 * mm, 53 * mm, "Java 17 - Spring Boot 3.2 - Spring Security - Thymeleaf - H2 / MySQL")
        canvas.drawString(22 * mm, 46 * mm, "Projet FitTrack - document de référence")
        canvas.restoreState()

    def content_page(self, canvas, doc):
        width, height = A4
        canvas.saveState()
        canvas.setFillColor(colors.HexColor("#F7F9FC"))
        canvas.rect(0, height - 16 * mm, width, 16 * mm, fill=1, stroke=0)
        canvas.setFillColor(colors.HexColor("#C6FF4A"))
        canvas.rect(0, height - 16 * mm, 6 * mm, 16 * mm, fill=1, stroke=0)
        canvas.setFillColor(DARK)
        canvas.setFont("Helvetica-Bold", 8.2)
        canvas.drawString(22 * mm, height - 10.5 * mm, "FitTrack")
        canvas.setFillColor(MUTED)
        canvas.setFont("Helvetica", 8.2)
        canvas.drawString(22 * mm + stringWidth("FitTrack ", "Helvetica-Bold", 8.2),
                          height - 10.5 * mm, "- Documentation technique")
        canvas.drawRightString(width - 22 * mm, height - 10.5 * mm, "Version 1.0")
        canvas.setStrokeColor(LINE)
        canvas.setLineWidth(0.6)
        canvas.line(22 * mm, height - 16 * mm, width - 22 * mm, height - 16 * mm)

        canvas.line(22 * mm, 14 * mm, width - 22 * mm, 14 * mm)
        canvas.setFillColor(MUTED)
        canvas.setFont("Helvetica", 8)
        canvas.drawString(22 * mm, 9 * mm, "FitTrack - Application de fitness tracking")
        canvas.drawRightString(width - 22 * mm, 9 * mm, "Page %d" % doc.page)
        canvas.restoreState()

    def afterFlowable(self, flowable):
        if isinstance(flowable, Paragraph):
            name = flowable.style.name
            if name == "H1":
                text = flowable.getPlainText()
                key = "h1-%d" % self.page
                self.canv.bookmarkPage(key)
                self.canv.addOutlineEntry(text, key, level=0, closed=False)
                self.notify("TOCEntry", (0, text, self.page))
            elif name == "H2":
                text = flowable.getPlainText()
                key = "h2-%s-%d" % (abs(hash(text)) % 100000, self.page)
                self.canv.bookmarkPage(key)
                self.canv.addOutlineEntry(text, key, level=1, closed=False)
                self.notify("TOCEntry", (1, text, self.page))


def build_story():
    story = [Spacer(1, 1), NextPageTemplate("content"), PageBreak()]

    # ----- Sommaire -----
    story.append(Paragraph("<font color='#79B700'>SOMMAIRE</font>", EYEBROW))
    story.append(Paragraph("Table des matières", H1_PLAIN))
    story.append(HRFlowable(width="100%", thickness=1.1, color=LINE,
                            spaceBefore=4, spaceAfter=12))
    toc = TableOfContents()
    toc.levelStyles = [
        ParagraphStyle("TOC1", fontName="Helvetica-Bold", fontSize=9.4, leading=15.5,
                       textColor=DARK),
        ParagraphStyle("TOC2", fontName="Helvetica", fontSize=8.2, leading=12.2,
                       leftIndent=14, textColor=MUTED),
    ]
    toc.dotsMinLevel = 0
    story.append(toc)
    story.append(Spacer(1, 10 * mm))
    story.append(callout(
        "À propos de ce document",
        "Cette documentation accompagne la version 1.0 de FitTrack. Elle presente le contexte du projet, "
        "les choix de conception, l'architecture logicielle, le modele de donnees, les algorithmes metier, "
        "la securite, les tests et un guide utilisateur. Le projet est livre avec son code source complet, "
        "son README, une presentation de soutenance et un jeu de donnees de demonstration."))
    story.append(PageBreak())

    # ----- 1. Contexte -----
    story += section_title("01", "Contexte et problematique")
    story.append(p("Le suivi de l'activite physique repose souvent sur des outils disperses : une application "
                   "de course pour le cardio, un carnet pour la musculation, une note pour le poids. Cette "
                   "dispersion empeche toute vision d'ensemble et rend la progression difficile a mesurer."))
    story.append(p("Le projet initial, Activity Management System, proposait un annuaire d'activites sportives "
                   "avec un systeme de notation. L'application repondait a un besoin de decouverte, mais ne "
                   "permettait ni de suivre une seance reelle, ni de mesurer une progression dans le temps."))
    story.append(Paragraph("Limites constatees de la version initiale", H3))
    story.extend(bullets([
        "Aucun historique : les activites etaient des fiches statiques, sans date ni repetition.",
        "Pas de notion de seance : impossible d'enregistrer duree, intensite, series ou charges.",
        "Pas de mesures corporelles ni d'objectifs : aucune vision de la progression physique.",
        "Interface limitee (Bootstrap 4 en CDN), peu adaptee au mobile et sans identite visuelle.",
        "Dependance forte a MySQL configure manuellement, ce qui freinait la prise en main.",
        "Identification des utilisateurs incomplete et absence de tests automatises.",
    ]))
    story.append(Spacer(1, 6))
    story.append(callout(
        "Problematique",
        "Comment transformer un annuaire d'activites en une veritable application de fitness tracking, "
        "capable d'enregistrer des seances detaillees, de calculer des indicateurs fiables et de montrer "
        "une progression dans le temps, tout en restant simple a installer et securisee ?",
        tone="blue"))

    # ----- 2. Objectifs -----
    story += section_title("02", "Objectifs et solution retenue")
    story.append(p("FitTrack est une reecriture complete de l'application autour d'un modele de donnees "
                   "oriente suivi d'entrainement. L'utilisateur enregistre ses seances, les exercices realises, "
                   "ses mesures corporelles et ses objectifs ; l'application calcule les indicateurs et les "
                   "presente dans un tableau de bord."))
    story.append(Paragraph("Objectifs fonctionnels", H3))
    story.extend(bullets([
        "Comptes utilisateurs autonomes (inscription, connexion, profil corporel, objectifs).",
        "Seances d'entrainement datees, typees, quantifiees, avec exercices et series detaillees.",
        "Bibliotheque d'exercices consultable et enrichissable par l'utilisateur.",
        "Mesures corporelles avec IMC, objectif de poids et courbe d'evolution.",
        "Tableau de bord synthetique : objectif hebdomadaire, calories, serie, records.",
        "Estimation des calories et du volume d'entrainement a partir de formules reconnues.",
    ]))
    story.append(Paragraph("Objectifs techniques", H3))
    story.extend(bullets([
        "Architecture Spring Boot en couches claire (web, service, repository, domain).",
        "Demarrage sans configuration de base de donnees (H2 embarque) et support MySQL en profil.",
        "Securite des comptes et isolation stricte des donnees par utilisateur.",
        "Vues Thymeleaf responsives, sans dependance front externe, graphiques generes cote serveur.",
        "Tests automatises des calculs metier et des parcours web sensibles.",
    ]))
    story.append(Spacer(1, 4))
    rows = [
        ["Annuaire d'activites", "Suivi d'entrainement personnel"],
        ["Fiches theoriques non datees", "Seances datees avec duree, intensite et exercices"],
        ["Notation libre", "Calories, volume, records et tendances"],
        ["Aucun suivi corporel", "Poids, IMC, mensurations et objectif"],
        ["MySQL obligatoire", "H2 embarque par defaut, MySQL en profil"],
        ["Interface de demonstration", "UI responsive sombre, tableau de bord analytique"],
    ]
    story.append(data_table(["Version initiale", "FitTrack"], rows,
                            [70 * mm, 96 * mm]))
    story.append(Paragraph("Tableau 1 - Positionnement de la refonte", CAPTION))
    story.append(PageBreak())

    # ----- 3. Fonctionnalites -----
    story += section_title("03", "Fonctionnalites")
    story.append(p("Les fonctionnalites sont organisees autour de cinq espaces accessibles depuis la navigation "
                   "principale : tableau de bord, seances, exercices, mesures et profil."))
    rows = [
        ["Authentification", "Inscription validee, connexion, deconnexion, roles, BCrypt, CSRF"],
        ["Tableau de bord", "4 KPI, objectif hebdomadaire, graphiques 7 jours et 8 semaines, poids, records"],
        ["Seances", "Creation, edition, suppression, filtres type et periode, detail des exercices"],
        ["Exercices", "Bibliotheque de 32 exercices, MET, groupes musculaires, recherche, ajout personnel"],
        ["Mesures", "Poids, masse grasse, mensurations, IMC, objectif, courbe d'evolution"],
        ["Profil", "Identite, age, taille, niveau d'activite, objectif hebdomadaire, MB et TDEE"],
        ["API REST", "GET /api/stats/summary pour une future application mobile"],
        ["Donnees de demo", "10 semaines d'historique : 40 seances, 10 mesures, 32 exercices"],
    ]
    story.append(data_table(["Module", "Contenu"], rows, [42 * mm, 124 * mm]))
    story.append(Paragraph("Tableau 2 - Modules fonctionnels", CAPTION))

    story.append(Paragraph("Parcours utilisateur", H2))
    steps = [
        ("1. Creation du compte", "Inscription en moins d'une minute, puis connexion securisee."),
        ("2. Profil", "Taille, date de naissance et objectif de seances par semaine."),
        ("3. Seance", "Type, duree, intensite, notes, puis ajout des exercices realises."),
        ("4. Mesures", "Poids et mensurations pour alimenter calories, IMC et courbe."),
        ("5. Analyse", "Tableau de bord, tendances et records pour ajuster la pratique."),
    ]
    cards = [card_box(title, [line], width=32 * mm) for title, line in steps]
    row = Table([cards], colWidths=[33 * mm] * 5)
    row.setStyle(TableStyle([("VALIGN", (0, 0), (-1, -1), "TOP"),
                             ("LEFTPADDING", (0, 0), (-1, -1), 0),
                             ("RIGHTPADDING", (0, 0), (-1, -1), 2)]))
    story.append(row)
    story.append(Paragraph("Figure 1 - Parcours utilisateur", CAPTION))
    story.append(PageBreak())

    # ----- 4. Architecture -----
    story += section_title("04", "Architecture technique")
    story.append(p("L'application suit une architecture MVC en couches, sans logique metier dans les vues. "
                   "Les controleurs valident les formulaires et delegue aux services ; les services appliquent "
                   "les regles metier et accedent aux donnees via Spring Data JPA."))
    layers = [
        ("Navigateur", "HTML / CSS - formulaires, tableaux de bord, graphiques CSS et SVG", DARK,
         colors.white),
        ("Spring Security", "Session, formulaire de connexion, BCrypt, protection CSRF", INK,
         colors.HexColor("#C6FF4A")),
        ("Couche web", "Controleurs MVC Thymeleaf, API REST JSON, DTO valides, GlobalModelAdvice",
         colors.HexColor("#1B3350"), colors.white),
        ("Couche service", "UserService, WorkoutService, MeasurementService, DashboardService, FitnessCalculator",
         colors.HexColor("#244766"), colors.white),
        ("Couche persistance", "Repositories Spring Data JPA, entites Hibernate, transactions",
         colors.HexColor("#2E5B75"), colors.white),
        ("Base de donnees", "H2 fichier local (defaut) - MySQL 8 en profil mysql", colors.HexColor("#3A7183"),
         colors.white),
    ]
    rows = []
    for title, subtitle, bg, fg in layers:
        rows.append([Paragraph("<b>%s</b>" % esc(title), ParagraphStyle(
            "LayerTitle", fontName="Helvetica-Bold", fontSize=9.4, leading=12,
            textColor=fg)),
            Paragraph(esc(subtitle), ParagraphStyle(
                "LayerText", fontName="Helvetica", fontSize=8.2, leading=11.5,
                textColor=fg))])
    table = Table(rows, colWidths=[46 * mm, 120 * mm])
    style = [("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
             ("LEFTPADDING", (0, 0), (-1, -1), 10),
             ("RIGHTPADDING", (0, 0), (-1, -1), 10),
             ("TOPPADDING", (0, 0), (-1, -1), 9),
             ("BOTTOMPADDING", (0, 0), (-1, -1), 9),
             ("GRID", (0, 0), (-1, -1), 2, colors.white)]
    for index, (_, _, bg, _) in enumerate(layers):
        style.append(("BACKGROUND", (0, index), (-1, index), bg))
    table.setStyle(TableStyle(style))
    story.append(table)
    story.append(Paragraph("Figure 2 - Architecture en couches", CAPTION))

    story.append(Paragraph("Organisation du code", H2))
    rows = [
        ["config", "SecurityConfig (chaine de filtres, BCrypt), DataSeeder (bibliotheque et compte demo)"],
        ["domain", "Entites JPA et enumerations metier (types, intensites, groupes musculaires)"],
        ["repository", "Interfaces Spring Data, requetes derivees et EntityGraph"],
        ["service", "Regles metier, transactions, calculs (FitnessCalculator, DashboardService)"],
        ["web", "Controleurs, DTO de formulaire, GlobalModelAdvice, DashboardView"],
        ["templates", "Fragments Thymeleaf, pages MVC, graphiques CSS/SVG"],
    ]
    story.append(data_table(["Paquet", "Responsabilite"], rows, [34 * mm, 132 * mm]))
    story.append(Paragraph("Tableau 3 - Organisation des paquets Java", CAPTION))
    story.append(PageBreak())

    # ----- 5. Modele de donnees -----
    story += section_title("05", "Modele de donnees")
    story.append(p("Le modele relationnel s'articule autour de l'utilisateur. Une seance appartient a un seul "
                   "utilisateur et contient plusieurs lignes d'exercices ; un exercice peut etre reutilise dans "
                   "plusieurs seances. Les mesures et les roles sont egalement rattaches a l'utilisateur."))
    entities = [
        ("users", ["id (PK)", "first_name, last_name", "email (unique)", "password (BCrypt)",
                   "birth_date, gender", "height_cm", "target_weight_kg", "weekly_goal", "activity_level"]),
        ("workouts", ["id (PK)", "user_id (FK)", "date", "type", "duration_minutes", "intensity",
                      "calories", "notes"]),
        ("workout_exercises", ["id (PK)", "workout_id (FK)", "exercise_id (FK)", "position",
                               "sets, reps", "weight_kg", "duration_seconds", "distance_km"]),
        ("measurements", ["id (PK)", "user_id (FK)", "date", "weight_kg", "body_fat_pct",
                          "waist_cm, chest_cm", "notes"]),
        ("exercises", ["id (PK)", "name", "muscle_group", "description", "met", "created_by (FK)"]),
        ("roles / users_roles", ["id (PK)", "name", "user_id, role_id", "ManyToMany"]),
    ]
    cards = [card_box(name, fields, width=51 * mm) for name, fields in entities]
    grid = Table([cards[0:3], cards[3:6]], colWidths=[54 * mm] * 3)
    grid.setStyle(TableStyle([("VALIGN", (0, 0), (-1, -1), "TOP"),
                              ("LEFTPADDING", (0, 0), (-1, -1), 0),
                              ("RIGHTPADDING", (0, 0), (-1, -1), 4),
                              ("BOTTOMPADDING", (0, 0), (-1, -1), 5)]))
    story.append(grid)
    story.append(Paragraph("Figure 3 - Entites principales et champs", CAPTION))

    story.append(Paragraph("Relations", H2))
    rows = [
        ["users - workouts", "1 a plusieurs", "Un utilisateur possede ses seances"],
        ["workouts - workout_exercises", "1 a plusieurs", "Composition de la seance, ordonnee par position"],
        ["exercises - workout_exercises", "1 a plusieurs", "Un exercice peut apparaitre dans plusieurs seances"],
        ["users - measurements", "1 a plusieurs", "Historique corporel de l'utilisateur"],
        ["users - roles", "plusieurs a plusieurs", "Table de jointure users_roles"],
        ["users - exercises", "1 a plusieurs (optionnel)", "Exercices personnels via created_by"],
    ]
    story.append(data_table(["Relation", "Cardinalite", "Regle"], rows,
                            [48 * mm, 36 * mm, 82 * mm]))
    story.append(Paragraph("Tableau 4 - Relations du modele", CAPTION))
    story.append(callout(
        "Isolation des donnees",
        "Toutes les requetes de lecture et d'ecriture sont filtrees par l'utilisateur authentifie "
        "(findByIdAndUser, findByUser...). Un identifiant appartenant a un autre compte renvoie une erreur "
        "404 ou 403, jamais les donnees d'autrui."))
    story.append(PageBreak())

    # ----- 6. Calculs -----
    story += section_title("06", "Calculs et algorithmes")
    story.append(p("Les indicateurs sont centralises dans la classe utilitaire FitnessCalculator, testee "
                   "unitairement, afin de garantir des resultats identiques dans l'interface, l'API et les tests."))
    rows = [
        ["Calories", "MET x facteur d'intensite x 3,5 x poids (kg) / 200 x duree (min)"],
        ["Volume souleve", "Somme des series x repetitions x charge (kg)"],
        ["IMC", "poids (kg) / taille (m) au carre"],
        ["Metabolisme de base", "10 x poids + 6,25 x taille - 5 x age + (5 ou -161 selon le genre)"],
        ["Depense totale", "Metabolisme de base x facteur d'activite (1,2 a 1,725)"],
        ["Serie", "Semaines consecutives contenant au moins une seance, en remontant depuis la semaine courante"],
    ]
    story.append(data_table(["Indicateur", "Formule"], rows, [42 * mm, 124 * mm]))
    story.append(Paragraph("Tableau 5 - Formules metier", CAPTION))

    story.append(Paragraph("Exemple de calcul calorique", H2))
    story.append(p("Pour une seance de musculation (MET 5,0) d'une heure, a intensite moderee (facteur 1,0), "
                   "pour un utilisateur de 80 kg : 5,0 x 1,0 x 3,5 x 80 / 200 x 60 = 420 kcal. "
                   "A intensite elevee (facteur 1,2), l'estimation passe a 504 kcal."))
    story.append(Paragraph("Serie de semaines", H2))
    story.append(p("Les dates de seances sont converties en lundis de semaine. Si la semaine courante contient "
                   "au moins une seance, le compteur demarre a cette semaine ; sinon il demarre a la semaine "
                   "precedente, ce qui evite de casser une serie en debut de semaine. Le compteur s'arrete "
                   "a la premiere semaine sans seance."))
    story.append(Paragraph("Generation des graphiques", H2))
    story.append(p("Les graphiques sont produits cote serveur, sans librairie JavaScript : les barres utilisent "
                   "des hauteurs en pourcentage calculees par DashboardService, et la courbe de poids est une "
                   "polyligne SVG dont les coordonnees sont normalisees entre le minimum et le maximum de la "
                   "serie. L'interface reste donc legere et fonctionne hors ligne."))
    story.append(callout(
        "Choix de conception",
        "Le calcul des calories est volontairement simple et explicable : il ne remplace pas une mesure de "
        "depense energetique reelle, mais il fournit un ordre de grandeur coherent et comparable d'une seance "
        "a l'autre, ce qui suffit pour suivre une tendance.",
        tone="blue"))
    story.append(PageBreak())

    # ----- 7. Securite -----
    story += section_title("07", "Securite et robustesse")
    story.extend(bullets([
        "Mots de passe haches avec BCrypt (force par defaut de Spring Security), jamais affiches ni journalises.",
        "Formulaire de connexion personnalise, messages d'erreur neutres (pas d'indication sur le champ invalide).",
        "CSRF active sur l'ensemble des formulaires POST ; les jetons sont injectes automatiquement par Thymeleaf.",
        "Autorisation par ressource : verification systematique du proprietaire avant lecture ou ecriture.",
        "Pages publiques limitees a l'accueil, la connexion, l'inscription et les ressources statiques.",
        "En-tetes de securite Spring Security, dont X-Frame-Options SAMEORIGIN pour la console H2 de developpement.",
        "Validation serveur de tous les formulaires (Jakarta Bean Validation) avec bornes explicites.",
        "Reponses d'erreur personnalisees et messages utilisateur en francais.",
    ]))
    story.append(Spacer(1, 5))
    story.append(Paragraph("Controle d'acces", H2))
    rows = [
        ["/", "Public", "Presentation de l'application"],
        ["/login, /register", "Public", "Authentification et creation de compte"],
        ["/dashboard, /workouts/**", "Authentifie", "Donnees strictement personnelles"],
        ["/exercises", "Authentifie", "Bibliotheque + exercices personnels"],
        ["/measurements, /profile", "Authentifie", "Mesures et objectifs du compte"],
        ["/api/stats/summary", "Authentifie (session)", "Statistiques JSON de l'utilisateur"],
        ["/h2-console", "Developpement", "Desactive par le profil MySQL"],
    ]
    story.append(data_table(["URL", "Acces", "Portee"], rows, [56 * mm, 36 * mm, 74 * mm]))
    story.append(Paragraph("Tableau 6 - Regles d'acces", CAPTION))
    story.append(PageBreak())

    # ----- 8. Tests -----
    story += section_title("08", "Qualite et tests")
    story.append(p("La suite de tests s'execute avec Maven et couvre les calculs metier ainsi que les parcours "
                   "web sensibles. Les tests d'integration utilisent une base H2 en memoire et le jeu de donnees "
                   "de demonstration."))
    rows = [
        ["FitnessCalculatorTest", "7", "Calories, intensite, IMC, metabolisme de base, serie hebdomadaire"],
        ["FitTrackApplicationTests", "5", "Contexte Spring, donnees de demo, acces public, redirection, dashboard, API"],
        ["Total", "12", "Execution : mvn test - resultat attendu : BUILD SUCCESS"],
    ]
    story.append(data_table(["Classe de test", "Tests", "Couverture"], rows,
                            [52 * mm, 18 * mm, 96 * mm], align_center=[1]))
    story.append(Paragraph("Tableau 7 - Suite de tests", CAPTION))

    story.append(Paragraph("Verifications realisees", H2))
    story.extend(bullets([
        "Compilation et empaquetage Maven sans erreur ni avertissement bloquant.",
        "Connexion reelle avec le compte de demonstration, redirection vers le tableau de bord.",
        "Rendu de toutes les pages : dashboard, seances, detail, edition, exercices, mesures, profil.",
        "Creation d'une seance via formulaire, ajout d'exercice, suppression, controle du volume.",
        "Reponse JSON de /api/stats/summary conforme au contrat documente.",
        "Acces aux pages protegees sans session : redirection vers la page de connexion.",
        "Verification des journaux applicatifs : aucune erreur de template.",
    ]))
    story.append(Spacer(1, 4))
    story.append(callout(
        "Reproductibilite",
        "Les tests utilisent une base en memoire : ils ne dependent d'aucune donnee externe et peuvent etre "
        "executes en integration continue. Le jeu de demonstration est cree par DataSeeder uniquement si la "
        "base est vide, ce qui evite les doublons au redemarrage."))
    story.append(PageBreak())

    # ----- 9. Installation -----
    story += section_title("09", "Installation et exploitation")
    story.append(Paragraph("Prerequis", H2))
    story.extend(bullets(["JDK 17 ou superieur", "Maven 3.8+ (ou wrapper Maven)",
                          "Aucune base de donnees requise pour le mode par defaut"]))
    story.append(Paragraph("Lancement en developpement", H2))
    commands = [
        ["mvn spring-boot:run", "Demarre l'application sur http://localhost:8080 avec H2"],
        ["mvn test", "Execute les 12 tests automatises"],
        ["mvn clean package", "Construit le JAR executable dans target/"],
        ["java -jar target/fittrack-1.0.0.jar", "Lance le JAR (port 8080 par defaut)"],
        ["mvn spring-boot:run -Dspring-boot.run.profiles=mysql", "Active le profil MySQL"],
    ]
    style_cell = ParagraphStyle("Code", fontName="Courier", fontSize=7.8, leading=10.5,
                                textColor=DARK)
    data = [[Paragraph("Commande", CELL_HEAD), Paragraph("Effet", CELL_HEAD)]]
    for command, effect in commands:
        data.append([Paragraph(command.replace(" ", "&nbsp;"), style_cell),
                     Paragraph(esc(effect), CELL)])
    code_table = Table(data, colWidths=[72 * mm, 94 * mm], repeatRows=1)
    code_table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), DARK),
        ("GRID", (0, 0), (-1, -1), 0.4, LINE),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, PANEL]),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("LEFTPADDING", (0, 0), (-1, -1), 7),
        ("TOPPADDING", (0, 0), (-1, -1), 6),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
    ]))
    story.append(code_table)
    story.append(Paragraph("Tableau 8 - Commandes principales", CAPTION))

    story.append(Paragraph("Configuration", H2))
    rows = [
        ["server.port", "8080", "Port HTTP (surchargeable par PORT)"],
        ["spring.datasource.url", "jdbc:h2:file:./data/fittrack", "Base H2 locale"],
        ["spring.jpa.hibernate.ddl-auto", "update", "Schema mis a jour automatiquement"],
        ["spring.web.locale", "fr", "Locale d'affichage"],
        ["app.seed.enabled", "true", "Jeu de donnees de demonstration"],
        ["spring.h2.console.enabled", "true", "Console H2 (desactiver en production)"],
    ]
    story.append(data_table(["Propriete", "Valeur", "Role"], rows, [52 * mm, 58 * mm, 56 * mm]))
    story.append(Paragraph("Tableau 9 - Configuration applicative", CAPTION))

    story.append(Paragraph("Passage en production", H2))
    story.extend(bullets([
        "Utiliser le profil MySQL (ou PostgreSQL) et desactiver la console H2.",
        "Desactiver app.seed.enabled pour ne pas creer le compte de demonstration.",
        "Servir l'application derriere un reverse proxy HTTPS.",
        "Definir server.forward-headers-strategy=framework pour les en-tetes proxy.",
        "Mettre en place la sauvegarde de la base et la rotation des journaux.",
    ]))
    story.append(PageBreak())

    # ----- 10. Guide -----
    story += section_title("10", "Guide utilisateur")
    story.append(Paragraph("Connexion a la demonstration", H2))
    story.append(p("Rendez-vous sur http://localhost:8080, puis utilisez le compte demo@fittrack.app avec le "
                   "mot de passe Demo1234!. Le compte contient dix semaines d'historique."))
    story.append(Paragraph("Enregistrer une seance", H2))
    story.extend(bullets([
        "Cliquer sur Nouvelle seance depuis la navigation ou le tableau de bord.",
        "Choisir la date, le type, la duree et l'intensite, puis valider.",
        "Sur la page de detail, ajouter chaque exercice : series, repetitions, charge ou duree.",
        "Le volume, les series totales et les calories estimes se mettent a jour immediatement.",
        "Le bouton Modifier permet de corriger la seance, Supprimer de la retirer.",
    ]))
    story.append(Paragraph("Suivre son poids", H2))
    story.extend(bullets([
        "Ouvrir l'onglet Mesures et saisir le poids du jour (seul champ obligatoire).",
        "Renseigner la masse grasse et les mensurations si disponibles.",
        "L'IMC, l'ecart avec l'objectif et la courbe se recalculent automatiquement.",
        "Le poids le plus recent sert de base au calcul des calories des nouvelles seances.",
    ]))
    story.append(Paragraph("Regler ses objectifs", H2))
    story.append(p("Dans Profil, definir l'objectif de seances par semaine (1 a 7) et le niveau d'activite. "
                   "Le tableau de bord affiche la progression, la serie de semaines actives et les records "
                   "personnels. Le calcul du metabolisme de base et de la depense totale y est egalement "
                   "presente a titre indicatif."))
    story.append(Spacer(1, 4))
    story.append(callout(
        "Astuce",
        "Une seance peut etre creee sans exercice, puis completee plus tard. C'est utile pour noter une seance "
        "immediatement apres l'effort et detailler les charges au calme."))
    story.append(PageBreak())

    # ----- 11. Roadmap -----
    story += section_title("11", "Limites et perspectives")
    story.append(p("FitTrack est une version 1.0 fonctionnelle. Les evolutions suivantes sont identifiees pour "
                   "prolonger le produit."))
    rows = [
        ["API REST complete", "CRUD seances et mesures, authentification par jeton", "Moyen"],
        ["Application mobile", "Client leger consommant l'API existante", "Eleve"],
        ["Planification", "Programmes, rappels et notifications", "Moyen"],
        ["Import / export", "CSV, montres connectees, plateformes de sante", "Moyen"],
        ["Objectifs avances", "Force, endurance, composition corporelle", "Moyen"],
        ["Administration", "Suivi global, gestion des exercices officiels", "Faible"],
        ["Internationalisation", "FR / EN, theme clair", "Faible"],
        ["Performance", "Cache, pagination, index sur les dates", "Faible"],
    ]
    story.append(data_table(["Evolution", "Description", "Effort"], rows,
                            [46 * mm, 100 * mm, 20 * mm], align_center=[2]))
    story.append(Paragraph("Tableau 10 - Perspectives d'evolution", CAPTION))

    story.append(Paragraph("Conclusion", H2))
    story.append(p("FitTrack transforme un exercice de style sur Spring MVC en une application de fitness "
                   "tracking complete : modele de donnees coherent, calculs metier documentes et testes, "
                   "interface responsive, securite par utilisateur et tableau de bord analytique. "
                   "Le projet demarre sans configuration de base de donnees, ce qui facilite la demonstration, "
                   "tout en restant deployable sur MySQL en production."))
    story.append(p("La conception en couches et la separation entre calculs metier, services et vues "
                   "permettent d'ajouter les evolutions prevues (API mobile, planification, import de donnees) "
                   "sans remettre en cause l'existant."))
    story.append(Spacer(1, 8 * mm))
    story.append(callout(
        "Livrables",
        "Code source complet (Maven, Java 17), README de prise en main, jeu de donnees de demonstration, "
        "documentation technique PDF, presentation de soutenance PowerPoint et archive ZIP du projet."))
    return story


if __name__ == "__main__":
    doc = FitTrackDoc(OUT)
    doc.multiBuild(build_story())
    print("PDF genere :", OUT)
