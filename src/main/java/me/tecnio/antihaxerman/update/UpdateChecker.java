/*
 *  Copyright (C) 2020 - 2021 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.update;

import lombok.Getter;
import me.tecnio.antihaxerman.AntiHaxerman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Getter
public final class UpdateChecker {

    private URL apiUrl;
    private String latestVersion;

    public UpdateChecker() {
        try {
            apiUrl = new URL("https://api.spigotmc.org/legacy/update.php?resource=83198");
        } catch (final Exception ignored) {
        }
    }

    public void checkUpdates() {
        AntiHaxerman.INSTANCE.getExecutorService().execute(() -> {
            try {
                AntiHaxerman.INSTANCE.setUpdateAvailable(isUpdateAvailable());
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isUpdateAvailable() throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

        connection.setConnectTimeout(5000);
        connection.connect();

        latestVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

        return !AntiHaxerman.INSTANCE.getVersion().equalsIgnoreCase(latestVersion);
    }
}
