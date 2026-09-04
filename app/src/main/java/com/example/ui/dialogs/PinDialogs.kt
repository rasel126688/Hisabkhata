package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.Localization

@Composable
fun SetupPinDialog(
    isBn: Boolean,
    onDismiss: () -> Unit,
    onPinSet: (String) -> Boolean
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Localization.tr("set_pin", isBn),
                fontWeight = FontWeight.Bold
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pin.length != 4) {
                        errorMessage = Localization.tr("enter_pin", isBn)
                        return@Button
                    }
                    if (pin != confirmPin) {
                        errorMessage = Localization.tr("pin_mismatch", isBn)
                        return@Button
                    }
                    val success = onPinSet(pin)
                    if (success) {
                        onDismiss()
                    }
                }
            ) {
                Text(Localization.tr("save", isBn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Localization.tr("cancel", isBn))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4) pin = it },
                    label = { Text(Localization.tr("enter_new_pin", isBn)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 4) confirmPin = it },
                    label = { Text(Localization.tr("confirm_pin", isBn)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        }
    )
}

@Composable
fun ChangePinDialog(
    isBn: Boolean,
    onDismiss: () -> Unit,
    onChangePin: (oldPin: String, newPin: String) -> Boolean
) {
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Localization.tr("change_pin", isBn),
                fontWeight = FontWeight.Bold
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (oldPin.length != 4 || newPin.length != 4) {
                        errorMessage = Localization.tr("enter_pin", isBn)
                        return@Button
                    }
                    val success = onChangePin(oldPin, newPin)
                    if (success) {
                        onDismiss()
                    } else {
                        errorMessage = Localization.tr("pin_invalid", isBn)
                    }
                }
            ) {
                Text(Localization.tr("save", isBn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Localization.tr("cancel", isBn))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = oldPin,
                    onValueChange = { if (it.length <= 4) oldPin = it },
                    label = { Text(Localization.tr("enter_pin", isBn)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 4) newPin = it },
                    label = { Text(Localization.tr("enter_new_pin", isBn)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        }
    )
}

@Composable
fun DisablePinDialog(
    isBn: Boolean,
    onDismiss: () -> Unit,
    onDisableConfirm: (pin: String) -> Boolean
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Localization.tr("disable_pin", isBn),
                fontWeight = FontWeight.Bold
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pin.length != 4) {
                        errorMessage = Localization.tr("enter_pin", isBn)
                        return@Button
                    }
                    val success = onDisableConfirm(pin)
                    if (success) {
                        onDismiss()
                    } else {
                        errorMessage = Localization.tr("pin_invalid", isBn)
                    }
                }
            ) {
                Text(Localization.tr("save", isBn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Localization.tr("cancel", isBn))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4) pin = it },
                    label = { Text(Localization.tr("enter_pin", isBn)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        }
    )
}
