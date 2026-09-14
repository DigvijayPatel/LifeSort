                NavigationDrawerItem(
                    label = { Text("Intentions") },
                    selected = selectedWindow == "Intentions",
                    onClick = {
                        selectedWindow = "Intentions"
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MangoWarm.copy(alpha = 0.2f),
                        selectedTextColor = MangoWarm,
                        unselectedTextColor = TextPure
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    label = { Text("Actions") },
                    selected = selectedWindow == "Actions",
                    onClick = {
                        selectedWindow = "Actions"
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MangoWarm.copy(alpha = 0.2f),
                        selectedTextColor = MangoWarm,
                        unselectedTextColor = TextPure
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    label = { Text("Recycle Bin") },
                    selected = selectedWindow == "Recycle Bin",
                    onClick = {
                        selectedWindow = "Recycle Bin"
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MangoWarm.copy(alpha = 0.2f),
                        selectedTextColor = MangoWarm,
                        unselectedTextColor = TextPure
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        if (selectedWindow == "Actions") {
            TaskHistoryScreen(
                completedTasks = completedTasks,
                onBack = { scope.launch { drawerState.open() } },
                isMenu = true
            )
        } else if (selectedWindow == "Recycle Bin") {
            RecycleBinScreen(
                deletedTasks = deletedTasks,
                onRestoreTask = onRestoreTask,
                onBack = { scope.launch { drawerState.open() } }
            )
        } else {
