@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shiftsync

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun ApprovalScreen() {
    val pending = AppState.swaps.filter { it.status == SwapStatus.WAIT_ADMIN }
    val history = AppState.swaps.filter { it.status != SwapStatus.WAIT_ADMIN }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Yêu cầu chờ duyệt (${pending.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        if (pending.isEmpty()) Text("Không có yêu cầu nào đang chờ.", color = Color.Gray)
        pending.forEach { r ->
            SwapCard(r) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { AppState.adminDecide(r.id, true) }) { Text("Duyệt") }
                    OutlinedButton(onClick = { AppState.adminDecide(r.id, false) }) { Text("Từ chối") }
                }
            }
        }
        Text("Lịch sử / đang chờ đồng nghiệp", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        if (history.isEmpty()) Text("Chưa có dữ liệu.", color = Color.Gray)
        history.forEach { SwapCard(it) }
    }
}

private fun money(v: Double): String = "%,d đ".format(v.toLong())

@Composable
fun StatsScreen(user: User) {
    val isAdmin = user.role == Role.ADMIN
    val today = LocalDate.now()
    var monthMode by remember { mutableStateOf(false) }
    val from = if (monthMode) today.withDayOfMonth(1) else AppState.mondayOf(today)
    val to = if (monthMode) from.plusMonths(1) else from.plusWeeks(1)

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !monthMode, onClick = { monthMode = false }, label = { Text("Tuần này") })
            FilterChip(selected = monthMode, onClick = { monthMode = true }, label = { Text("Tháng này") })
        }
        Text("${from.format(DATE_FMT)} – ${to.minusDays(1).format(DATE_FMT)}", color = Color.Gray)

        if (!isAdmin) {
            val h = AppState.hours(user.id, from, to)
            val n = AppState.shiftCount(user.id, from, to)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatBox("Số ca", "$n", Modifier.weight(1f))
                StatBox("Giờ làm", "%.1f h".format(h), Modifier.weight(1f))
            }
            StatBox("Lương dự kiến (${money(user.hourlyRate.toDouble())}/giờ)", money(h * user.hourlyRate), Modifier.fillMaxWidth())

            Text(if (monthMode) "Giờ làm theo tuần" else "Giờ làm theo ngày", fontWeight = FontWeight.Bold)
            val bars = if (!monthMode) (0..6).map { d ->
                val day = from.plusDays(d.toLong())
                DAY_SHORT[d] to AppState.hours(user.id, day, day.plusDays(1))
            } else {
                val list = mutableListOf<Pair<String, Double>>()
                var w = AppState.mondayOf(from)
                var i = 1
                while (w.isBefore(to)) {
                    val s = if (w.isBefore(from)) from else w
                    val e = if (w.plusWeeks(1).isAfter(to)) to else w.plusWeeks(1)
                    list.add("Tuần $i" to AppState.hours(user.id, s, e))
                    w = w.plusWeeks(1); i++
                }
                list
            }
            BarChart(bars)
        } else {
            val staff = AppState.users.filter { it.role == Role.STAFF }
            val open = AppState.shifts.filter { !it.date.isBefore(from) && it.date.isBefore(to) }.sumOf { it.capacity - it.staff.size }
            StatBox("Chỗ còn trống trong kỳ", "$open", Modifier.fillMaxWidth())
            var total = 0.0
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        Text("Nhân viên", Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Ca", Modifier.weight(0.6f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Giờ", Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Lương", Modifier.weight(1.6f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    staff.forEach { u ->
                        val h = AppState.hours(u.id, from, to)
                        total += h * u.hourlyRate
                        Row(Modifier.fillMaxWidth()) {
                            Text(u.name, Modifier.weight(2f), fontSize = 12.sp)
                            Text("${AppState.shiftCount(u.id, from, to)}", Modifier.weight(0.6f), fontSize = 12.sp)
                            Text("%.1f".format(h), Modifier.weight(0.8f), fontSize = 12.sp)
                            Text(money(h * u.hourlyRate), Modifier.weight(1.6f), fontSize = 12.sp)
                        }
                    }
                    Text("Tổng chi lương dự kiến: ${money(total)}", fontWeight = FontWeight.Bold)
                }
            }
            Text("Giờ làm theo nhân viên", fontWeight = FontWeight.Bold)
            BarChart(staff.map { it.name.substringAfterLast(' ') to AppState.hours(it.id, from, to) })
        }
    }
}

@Composable
private fun StatBox(title: String, value: String, modifier: Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun BarChart(data: List<Pair<String, Double>>) {
    val max = (data.maxOfOrNull { it.second } ?: 0.0).coerceAtLeast(1.0)
    Row(
        Modifier.fillMaxWidth().height(160.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, v) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                Text(if (v > 0) "%.1f".format(v) else "", fontSize = 10.sp)
                Box(
                    Modifier.width(24.dp).height((100 * v / max).dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                )
                Text(label, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Giới hạn số ca / tuần / nhân viên", fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { if (AppState.weeklyLimit > 1) AppState.weeklyLimit-- }) { Text("−") }
                    Text("${AppState.weeklyLimit} ca", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    OutlinedButton(onClick = { if (AppState.weeklyLimit < 21) AppState.weeklyLimit++ }) { Text("+") }
                }
                Text("Nhân viên chạm mức này sẽ bị cảnh báo và không thể chọn thêm ca.", fontSize = 12.sp, color = Color.Gray)
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tạo lịch ca", fontWeight = FontWeight.Bold)
                Text("Mỗi tuần gồm 3 ca/ngày (Sáng, Chiều, Tối), sức chứa mặc định 2 người/ca. Chỉnh sức chứa từng ca ở tab Lịch.", fontSize = 12.sp)
                Button(onClick = {
                    val last = AppState.shifts.maxOf { it.date }
                    AppState.ensureWeek(AppState.mondayOf(last).plusWeeks(1))
                    Toast.makeText(ctx, "Đã tạo lịch tuần kế tiếp", Toast.LENGTH_SHORT).show()
                }) { Text("➕ Tạo lịch tuần kế tiếp") }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Nhân viên & mức lương theo giờ", fontWeight = FontWeight.Bold)
                AppState.users.filter { it.role == Role.STAFF }.forEach {
                    Text("• ${it.name} — ${money(it.hourlyRate.toDouble())}/giờ", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(user: User) {
    val list = AppState.notifications.filter { it.userId == user.id }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(onClick = {
            for (i in AppState.notifications.indices) {
                val n = AppState.notifications[i]
                if (n.userId == user.id && !n.read) AppState.notifications[i] = n.copy(read = true)
            }
        }) { Text("Đánh dấu tất cả đã đọc") }
        if (list.isEmpty()) Text("Chưa có thông báo.", color = Color.Gray)
        list.forEach { n ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(n.text, fontWeight = if (n.read) FontWeight.Normal else FontWeight.Bold, fontSize = 14.sp)
                    Text(n.time.format(DATETIME_FMT), fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}
