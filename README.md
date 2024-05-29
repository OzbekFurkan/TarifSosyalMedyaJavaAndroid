Bu bir sosyal medya tarif uygulamasıdır. Home kısmında tarifleri listeleyebilir, Search kısmında tarif aratabilir, Add Content kısmında tarif ekleyebilir, 
Favorites kısmında favori tarifleri görüntüleyebilir ve Profil kısmında profilinizi görüntüleyerek kendi paylaşımlarınızı görüntüleyebilirsiniz. Contenlerin listelenmesi için
Recyclerview kullanıldı ve hepsi için bir adapter bulunuyor, bu adapter'in constructor'ına farklı Content listleri argüman olarak yollayarak contentlerimizi listeliyoruz.
Veritabanı için Firebase kullanıldı. Görsel tutmak için Storage, Authentication için Fİrebase Authentication, diğer datalar için Firestore kullanıldı. 
Firebase Firestore yapısı aşağıdaki gibi görünüyor.

<img width="1136" alt="Screenshot 2024-05-29 at 14 39 29" src="https://github.com/OzbekFurkan/TarifSosyalMedyaJavaAndroid/assets/104983850/a14c2279-195b-4489-972e-290c3d331d94">

<img width="1131" alt="Screenshot 2024-05-29 at 14 39 48" src="https://github.com/OzbekFurkan/TarifSosyalMedyaJavaAndroid/assets/104983850/bb60865b-ead5-42a3-be5a-d3acf05f9f0f">

<img width="1133" alt="Screenshot 2024-05-29 at 14 40 01" src="https://github.com/OzbekFurkan/TarifSosyalMedyaJavaAndroid/assets/104983850/64826008-cf39-4c8e-a663-640be58eddf2">

Burada ingredients(malzemeler) collection'ında dataları kendimiz girmemiz gerekiyor (yani bu collection'ı biz oluşturuyoruz ve auto id ile document olarak malzemelerimizi giriyoruz).
Mazelemelerdeki 'price_link' field'ı 'cimri.com' sitesinden JSoup ile Webscrapping yaparak fiyat çekmek için gereklidir. Malzeme datası girerken bu siteden o malzemeyi bulup 
linkini atmanız yeterlidir.
Not: Bu veritabanını kullanabilmeniz için Firebase console'da kendi projenizi oluşturup indireceğiniz sdk (.json) dosyasını 'app' klasörüne atmalısınız.
