import re

with open('app/src/main/AndroidManifest.xml', 'r') as f:
    content = f.read()

# Add enableOnBackInvokedCallback to application
content = content.replace(
    'android:theme="@style/Theme.MyApplication">',
    'android:theme="@style/Theme.MyApplication"\n        android:enableOnBackInvokedCallback="true">'
)

# Add windowSoftInputMode to activity
content = content.replace(
    'android:theme="@style/Theme.MyApplication">',
    'android:theme="@style/Theme.MyApplication"\n            android:windowSoftInputMode="adjustResize">'
)

with open('app/src/main/AndroidManifest.xml', 'w') as f:
    f.write(content)
