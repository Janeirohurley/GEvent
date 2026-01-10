# GEvent - Modèles Django pour l'API Backend

Ce document contient tous les modèles Django nécessaires pour créer la base de données backend de l'application GEvent.

## Table des matières
1. [Configuration](#configuration)
2. [Modèles](#modèles)
3. [Endpoints API](#endpoints-api)
4. [Installation](#installation)

---

## Configuration

### Requirements
```txt
Django==5.0
djangorestframework==3.14.0
django-cors-headers==4.3.1
Pillow==10.2.0
```

### Settings Django
```python
# settings.py

INSTALLED_APPS = [
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'rest_framework',
    'corsheaders',
    'events',  # Votre app principale
]

MIDDLEWARE = [
    'corsheaders.middleware.CorsMiddleware',
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
]

CORS_ALLOW_ALL_ORIGINS = True  # Pour le développement

REST_FRAMEWORK = {
    'DEFAULT_AUTHENTICATION_CLASSES': [
        'rest_framework.authentication.TokenAuthentication',
    ],
    'DEFAULT_PERMISSION_CLASSES': [
        'rest_framework.permissions.IsAuthenticated',
    ],
}

MEDIA_URL = '/media/'
MEDIA_ROOT = os.path.join(BASE_DIR, 'media')
```

---

## Modèles

### 1. Modèle User (Utilisateur)

```python
# models.py

from django.contrib.auth.models import AbstractUser
from django.db import models

class User(AbstractUser):
    """
    Modèle utilisateur personnalisé pour GEvent
    """
    phone_number = models.CharField(max_length=20, blank=True, null=True)
    profile_image = models.ImageField(upload_to='profiles/', blank=True, null=True)
    bio = models.TextField(blank=True, null=True)
    date_of_birth = models.DateField(blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'users'
        ordering = ['-created_at']

    def __str__(self):
        return self.username
```

### 2. Modèle Category (Catégorie)

```python
class Category(models.Model):
    """
    Catégories d'événements (Musique, Sport, Théâtre, etc.)
    """
    name = models.CharField(max_length=100, unique=True)
    description = models.TextField(blank=True, null=True)
    icon = models.CharField(max_length=50, blank=True, null=True)  # Nom de l'icône
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'categories'
        ordering = ['name']
        verbose_name_plural = 'Categories'

    def __str__(self):
        return self.name
```

### 3. Modèle Event (Événement)

```python
class Event(models.Model):
    """
    Modèle principal pour les événements
    """
    STATUS_CHOICES = [
        ('upcoming', 'À venir'),
        ('ongoing', 'En cours'),
        ('completed', 'Terminé'),
        ('cancelled', 'Annulé'),
    ]

    title = models.CharField(max_length=255)
    description = models.TextField()
    category = models.ForeignKey(Category, on_delete=models.CASCADE, related_name='events')
    image_url = models.ImageField(upload_to='events/', blank=True, null=True)

    # Informations de localisation
    location = models.CharField(max_length=255)
    latitude = models.DecimalField(max_digits=9, decimal_places=6, blank=True, null=True)
    longitude = models.DecimalField(max_digits=9, decimal_places=6, blank=True, null=True)

    # Informations temporelles
    date = models.DateTimeField()
    end_date = models.DateTimeField(blank=True, null=True)
    duration = models.CharField(max_length=50, blank=True, null=True)  # Ex: "2h30"

    # Informations de tarification
    is_free = models.BooleanField(default=False)
    price = models.DecimalField(max_digits=10, decimal_places=2, default=0.00)
    currency = models.CharField(max_length=3, default='BIF')  # Franc Burundais

    # Capacité et disponibilité
    total_capacity = models.IntegerField(default=0)
    available_seats = models.IntegerField(default=0)

    # Organisateur
    organizer = models.ForeignKey(User, on_delete=models.CASCADE, related_name='organized_events')
    organizer_name = models.CharField(max_length=255, blank=True, null=True)
    organizer_image = models.ImageField(upload_to='organizers/', blank=True, null=True)

    # Statut et métriques
    status = models.CharField(max_length=20, choices=STATUS_CHOICES, default='upcoming')
    is_popular = models.BooleanField(default=False)
    rating = models.DecimalField(max_digits=3, decimal_places=2, default=0.00)
    total_reviews = models.IntegerField(default=0)

    # Timestamps
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'events'
        ordering = ['-date']
        indexes = [
            models.Index(fields=['date', 'status']),
            models.Index(fields=['category', 'is_popular']),
        ]

    def __str__(self):
        return self.title

    @property
    def is_sold_out(self):
        return self.available_seats <= 0
```

### 4. Modèle EventImage (Images supplémentaires)

```python
class EventImage(models.Model):
    """
    Images supplémentaires pour un événement
    """
    event = models.ForeignKey(Event, on_delete=models.CASCADE, related_name='images')
    image = models.ImageField(upload_to='events/gallery/')
    caption = models.CharField(max_length=255, blank=True, null=True)
    order = models.IntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'event_images'
        ordering = ['order', 'created_at']

    def __str__(self):
        return f"Image for {self.event.title}"
```

### 5. Modèle Attendee (Participants)

```python
class Attendee(models.Model):
    """
    Participants confirmés à un événement
    """
    event = models.ForeignKey(Event, on_delete=models.CASCADE, related_name='attendees')
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='attending_events')
    profile_image = models.ImageField(upload_to='attendees/', blank=True, null=True)
    joined_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'attendees'
        unique_together = ['event', 'user']
        ordering = ['-joined_at']

    def __str__(self):
        return f"{self.user.username} - {self.event.title}"
```

### 6. Modèle Ticket (Billet)

```python
class Ticket(models.Model):
    """
    Billets achetés par les utilisateurs
    """
    STATUS_CHOICES = [
        ('confirmed', 'Confirmé'),
        ('cancelled', 'Annulé'),
        ('used', 'Utilisé'),
        ('expired', 'Expiré'),
    ]

    code = models.CharField(max_length=50, unique=True)
    event = models.ForeignKey(Event, on_delete=models.CASCADE, related_name='tickets')
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='tickets')

    # Informations du détenteur
    holder_name = models.CharField(max_length=255)
    holder_email = models.EmailField()
    holder_phone = models.CharField(max_length=20, blank=True, null=True)

    # Informations du billet
    seat = models.CharField(max_length=50, blank=True, null=True)
    price = models.DecimalField(max_digits=10, decimal_places=2)
    currency = models.CharField(max_length=3, default='BIF')

    # QR Code pour validation
    qr_code = models.ImageField(upload_to='qrcodes/', blank=True, null=True)

    # Statut et dates
    status = models.CharField(max_length=20, choices=STATUS_CHOICES, default='confirmed')
    purchase_date = models.DateTimeField(auto_now_add=True)
    used_at = models.DateTimeField(blank=True, null=True)
    cancelled_at = models.DateTimeField(blank=True, null=True)

    # Métadonnées
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'tickets'
        ordering = ['-purchase_date']
        indexes = [
            models.Index(fields=['code']),
            models.Index(fields=['user', 'status']),
        ]

    def __str__(self):
        return f"Ticket {self.code} - {self.event.title}"

    def save(self, *args, **kwargs):
        if not self.code:
            # Générer un code unique
            import uuid
            self.code = f"TKT-{uuid.uuid4().hex[:12].upper()}"
        super().save(*args, **kwargs)
```

### 7. Modèle Order (Commande)

```python
class Order(models.Model):
    """
    Commandes de billets
    """
    PAYMENT_STATUS_CHOICES = [
        ('pending', 'En attente'),
        ('completed', 'Complété'),
        ('failed', 'Échoué'),
        ('refunded', 'Remboursé'),
    ]

    order_number = models.CharField(max_length=50, unique=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='orders')
    event = models.ForeignKey(Event, on_delete=models.CASCADE, related_name='orders')

    # Informations de paiement
    quantity = models.IntegerField(default=1)
    unit_price = models.DecimalField(max_digits=10, decimal_places=2)
    total_amount = models.DecimalField(max_digits=10, decimal_places=2)
    currency = models.CharField(max_length=3, default='BIF')

    payment_method = models.CharField(max_length=50)  # Ex: "credit_card", "mobile_money"
    payment_status = models.CharField(max_length=20, choices=PAYMENT_STATUS_CHOICES, default='pending')
    payment_date = models.DateTimeField(blank=True, null=True)
    transaction_id = models.CharField(max_length=255, blank=True, null=True)

    # Timestamps
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'orders'
        ordering = ['-created_at']
        indexes = [
            models.Index(fields=['order_number']),
            models.Index(fields=['user', 'payment_status']),
        ]

    def __str__(self):
        return f"Order {self.order_number}"

    def save(self, *args, **kwargs):
        if not self.order_number:
            # Générer un numéro de commande unique
            import uuid
            self.order_number = f"ORD-{uuid.uuid4().hex[:12].upper()}"
        super().save(*args, **kwargs)
```

### 8. Modèle Review (Avis)

```python
class Review(models.Model):
    """
    Avis et évaluations des événements
    """
    event = models.ForeignKey(Event, on_delete=models.CASCADE, related_name='reviews')
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='reviews')

    rating = models.IntegerField(choices=[(i, i) for i in range(1, 6)])  # 1-5 étoiles
    comment = models.TextField(blank=True, null=True)

    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'reviews'
        unique_together = ['event', 'user']
        ordering = ['-created_at']

    def __str__(self):
        return f"{self.user.username} - {self.event.title} ({self.rating}★)"
```

### 9. Modèle Favorite (Favoris)

```python
class Favorite(models.Model):
    """
    Événements favoris des utilisateurs
    """
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='favorites')
    event = models.ForeignKey(Event, on_delete=models.CASCADE, related_name='favorited_by')
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'favorites'
        unique_together = ['user', 'event']
        ordering = ['-created_at']

    def __str__(self):
        return f"{self.user.username} - {self.event.title}"
```

---

## Endpoints API

### Authentification
```
POST   /api/auth/register/           # Inscription
POST   /api/auth/login/              # Connexion
POST   /api/auth/logout/             # Déconnexion
GET    /api/auth/user/               # Profil utilisateur
PUT    /api/auth/user/               # Mise à jour profil
```

### Événements
```
GET    /api/events/                  # Liste tous les événements
GET    /api/events/upcoming/         # Événements à venir
GET    /api/events/popular/          # Événements populaires
GET    /api/events/<id>/             # Détails d'un événement
POST   /api/events/                  # Créer un événement (organizer only)
PUT    /api/events/<id>/             # Modifier un événement
DELETE /api/events/<id>/             # Supprimer un événement
GET    /api/events/<id>/attendees/   # Liste des participants
```

### Catégories
```
GET    /api/categories/              # Liste des catégories
GET    /api/categories/<id>/events/  # Événements par catégorie
```

### Tickets
```
GET    /api/tickets/                 # Mes billets
GET    /api/tickets/<id>/            # Détails d'un billet
GET    /api/tickets/upcoming/        # Billets à venir
GET    /api/tickets/completed/       # Billets terminés
GET    /api/tickets/cancelled/       # Billets annulés
POST   /api/tickets/<id>/cancel/     # Annuler un billet
```

### Commandes
```
GET    /api/orders/                  # Mes commandes
POST   /api/orders/                  # Créer une commande
GET    /api/orders/<id>/             # Détails d'une commande
PUT    /api/orders/<id>/payment/     # Mettre à jour le paiement
```

### Avis
```
GET    /api/events/<id>/reviews/     # Avis d'un événement
POST   /api/events/<id>/reviews/     # Ajouter un avis
PUT    /api/reviews/<id>/            # Modifier un avis
DELETE /api/reviews/<id>/            # Supprimer un avis
```

### Favoris
```
GET    /api/favorites/               # Mes favoris
POST   /api/favorites/               # Ajouter aux favoris
DELETE /api/favorites/<id>/          # Retirer des favoris
```

---

## Installation

### 1. Créer le projet Django

```bash
# Créer un environnement virtuel
python -m venv venv
source venv/bin/activate  # Sur Windows: venv\Scripts\activate

# Installer Django et dépendances
pip install django djangorestframework django-cors-headers pillow

# Créer le projet
django-admin startproject gevent_backend
cd gevent_backend

# Créer l'application
python manage.py startapp events
```

### 2. Configuration

Copiez les modèles ci-dessus dans `events/models.py`

Configurez `settings.py` comme indiqué dans la section Configuration

### 3. Créer les migrations

```bash
python manage.py makemigrations
python manage.py migrate
```

### 4. Créer un superutilisateur

```bash
python manage.py createsuperuser
```

### 5. Lancer le serveur

```bash
python manage.py runserver
```

---

## Serializers (À créer)

### Exemple de Serializer pour Event

```python
# serializers.py

from rest_framework import serializers
from .models import Event, Category, User, Attendee

class CategorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Category
        fields = ['id', 'name', 'description', 'icon']

class AttendeeSerializer(serializers.ModelSerializer):
    username = serializers.CharField(source='user.username', read_only=True)

    class Meta:
        model = Attendee
        fields = ['id', 'username', 'profile_image']

class EventSerializer(serializers.ModelSerializer):
    category = CategorySerializer(read_only=True)
    category_id = serializers.PrimaryKeyRelatedField(
        queryset=Category.objects.all(),
        source='category',
        write_only=True
    )
    attendees = AttendeeSerializer(many=True, read_only=True)
    attendee_count = serializers.SerializerMethodField()

    class Meta:
        model = Event
        fields = [
            'id', 'title', 'description', 'category', 'category_id',
            'image_url', 'location', 'latitude', 'longitude',
            'date', 'end_date', 'duration',
            'is_free', 'price', 'currency',
            'total_capacity', 'available_seats',
            'organizer_name', 'organizer_image',
            'status', 'is_popular', 'rating', 'total_reviews',
            'attendees', 'attendee_count',
            'created_at', 'updated_at'
        ]
        read_only_fields = ['rating', 'total_reviews', 'created_at', 'updated_at']

    def get_attendee_count(self, obj):
        return obj.attendees.count()
```

---

## Views (À créer)

### Exemple de ViewSet pour Event

```python
# views.py

from rest_framework import viewsets, filters, status
from rest_framework.decorators import action
from rest_framework.response import Response
from django_filters.rest_framework import DjangoFilterBackend
from .models import Event
from .serializers import EventSerializer

class EventViewSet(viewsets.ModelViewSet):
    queryset = Event.objects.all()
    serializer_class = EventSerializer
    filter_backends = [DjangoFilterBackend, filters.SearchFilter, filters.OrderingFilter]
    filterset_fields = ['category', 'status', 'is_free', 'is_popular']
    search_fields = ['title', 'description', 'location']
    ordering_fields = ['date', 'price', 'rating', 'created_at']
    ordering = ['-date']

    @action(detail=False, methods=['get'])
    def upcoming(self, request):
        """Événements à venir"""
        upcoming_events = self.queryset.filter(status='upcoming').order_by('date')
        serializer = self.get_serializer(upcoming_events, many=True)
        return Response(serializer.data)

    @action(detail=False, methods=['get'])
    def popular(self, request):
        """Événements populaires"""
        popular_events = self.queryset.filter(is_popular=True)
        serializer = self.get_serializer(popular_events, many=True)
        return Response(serializer.data)

    @action(detail=True, methods=['get'])
    def attendees(self, request, pk=None):
        """Liste des participants"""
        event = self.get_object()
        attendees = event.attendees.all()
        from .serializers import AttendeeSerializer
        serializer = AttendeeSerializer(attendees, many=True)
        return Response(serializer.data)
```

---

## URLs (À configurer)

```python
# urls.py

from django.contrib import admin
from django.urls import path, include
from rest_framework.routers import DefaultRouter
from events.views import EventViewSet

router = DefaultRouter()
router.register(r'events', EventViewSet)

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/', include(router.urls)),
    path('api/auth/', include('rest_framework.urls')),
]
```

---

## Notes Importantes

### 1. Génération de QR Codes

Pour générer des QR codes pour les billets, installez:
```bash
pip install qrcode[pil]
```

Exemple dans le modèle Ticket:
```python
import qrcode
from io import BytesIO
from django.core.files import File

def generate_qr_code(self):
    qr = qrcode.QRCode(version=1, box_size=10, border=5)
    qr.add_data(self.code)
    qr.make(fit=True)
    img = qr.make_image(fill_color="black", back_color="white")

    buffer = BytesIO()
    img.save(buffer, format='PNG')
    file_name = f'qr_{self.code}.png'
    self.qr_code.save(file_name, File(buffer), save=False)
```

### 2. Gestion des Images

Assurez-vous de configurer MEDIA_URL et MEDIA_ROOT dans settings.py et d'ajouter les routes pour servir les médias en développement:

```python
# urls.py
from django.conf import settings
from django.conf.urls.static import static

urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
```

### 3. Authentification

Utilisez Django Rest Framework Token Authentication ou JWT pour sécuriser l'API:

```bash
pip install djangorestframework-simplejwt
```

### 4. Données de Test

Créez un fichier de fixtures ou un script pour générer des données de test:

```python
# management/commands/populate_db.py
from django.core.management.base import BaseCommand
from events.models import Category, Event, User
from datetime import datetime, timedelta

class Command(BaseCommand):
    help = 'Populate database with test data'

    def handle(self, *args, **kwargs):
        # Créer des catégories
        categories = ['Musique', 'Sport', 'Théâtre', 'Conférence']
        for cat_name in categories:
            Category.objects.get_or_create(name=cat_name)

        self.stdout.write(self.style.SUCCESS('Database populated!'))
```

Exécutez avec:
```bash
python manage.py populate_db
```

---

## Support

Pour toute question ou clarification sur ces modèles, contactez l'équipe de développement.

**Dernière mise à jour:** 2026-01-09
**Version:** 1.0.0
