import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

import_statement = "import androidx.compose.runtime.LaunchedEffect\n"
if "import androidx.compose.runtime.LaunchedEffect" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.runtime.Composable\n" + import_statement)

permission_logic = """
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle permission
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
"""

content = content.replace(
    "    val currentScreen by viewModel.currentScreen.collectAsState()",
    permission_logic + "    val currentScreen by viewModel.currentScreen.collectAsState()"
)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
