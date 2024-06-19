package idk.team.network;

import idk.team.IDK;
import idk.team.IDKMessageConfig;
import idk.team.JSON.decoder;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class IDKnetHandler implements Runnable {
    private static final IDKMessageConfig idkMessageConfig = new IDKMessageConfig(IDK.idk, "message.yml") {
        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }
    };
    private static String download_source = IDK.idk.getConfig().getString("download-source");

    private static void refresh_download_source() {
        download_source = IDK.idk.getConfig().getString("download-source");
    }

    public static void get_10_top_projects(CommandSender commandSender) throws IOException {
        if (IDK.idk.debug) {
            if (commandSender instanceof Player) {
                commandSender.sendMessage("commandSender=Player");
            } else {
                commandSender.sendMessage("commandSender=Console");
            }
        }
        refresh_download_source();
        if (Objects.equals(download_source, "papermc")) {
            commandSender.sendMessage("Getting latest 10 plugins from papermc...");
            int limit = 10;
            if (IDK.idk.debug) {
                commandSender.sendMessage("limit="+limit);
            }
            String version = Bukkit.getServer().getMinecraftVersion();
            if (IDK.idk.debug) {
                commandSender.sendMessage("version="+version);
            }
            String url = "https://hangar.papermc.io/api/v1/projects?limit="+limit+"&version="+version;
            if (IDK.idk.debug) {
                commandSender.sendMessage("url="+url);
            }
            String result = sendGet(url);
            commandSender.sendMessage("10 Top Plugins:");
            for(int i = 0; i < 10; i++) {
                String plugin_title = decoder.decode_json(result, i, "result", "name");
                String plugin_description = decoder.decode_json(result, i, "result", "description");
                int a = i+1;
                commandSender.sendMessage(a+". Name: " + plugin_title + "\nDescription: " + plugin_description);
            }
            commandSender.sendMessage("Install a plugin by using: /idk plugin install <name>");
        }
        else if (Objects.equals(download_source, "modrinth")) {
            commandSender.sendMessage("Getting latest 10 plugins from modrinth...");
            int limit = 10;
            if (IDK.idk.debug) {
                commandSender.sendMessage("limit="+limit);
            }
            String url = "https://api.modrinth.com/v2/search?limit="+limit+"&facets=";
            if (IDK.idk.debug) {
                commandSender.sendMessage("url="+url);
            }
            String categories = "paper";
            if (IDK.idk.debug) {
                commandSender.sendMessage("categories="+categories);
            }
            String versions = Bukkit.getServer().getMinecraftVersion();
            if (IDK.idk.debug) {
                commandSender.sendMessage("versions="+versions);
            }
            String arg = "[[\"categories:"+categories+"\"],[\"versions:"+versions+"\"],[\"project_type:plugin\"]]";
            if (IDK.idk.debug) {
                commandSender.sendMessage("arg="+arg);
            }
            String real_uri = argument_handler(url, arg);
            if (IDK.idk.debug) {
                commandSender.sendMessage("real_uri="+real_uri);
            }
            String result = sendGet(real_uri);
            commandSender.sendMessage("10 Top Plugins:");
            for(int i = 0; i < 10; i++) {
                String plugin_title = decoder.decode_json(result, i, "hits", "title");
                String plugin_description = decoder.decode_json(result, i, "hits", "description");
                String plugin_id = decoder.decode_json(result, i, "hits", "project_id");
                int a = i+1;
                commandSender.sendMessage(a+". Name: " + plugin_title + "\nID: " + plugin_id + "\nDescription: " + plugin_description);
            }
            commandSender.sendMessage("Install a plugin by using: /idk plugin install <name>");
        }
        else if (Objects.equals(download_source, "both")) {
            try {
                commandSender.sendMessage("Getting latest 10 plugins from papermc...");
                int limit = 10;
                if (IDK.idk.debug) {
                    commandSender.sendMessage("limit="+limit);
                }
                String version = Bukkit.getServer().getMinecraftVersion();
                if (IDK.idk.debug) {
                    commandSender.sendMessage("version="+version);
                }
                String url = "https://hangar.papermc.io/api/v1/projects?limit="+limit+"&version="+version;
                if (IDK.idk.debug) {
                    commandSender.sendMessage("url="+url);
                }
                String result = sendGet(url);
                commandSender.sendMessage("10 Top Plugins:");
                for(int i = 0; i < 10; i++) {
                    String plugin_title = decoder.decode_json(result, i, "result", "name");
                    String plugin_description = decoder.decode_json(result, i, "result", "description");
                    int a = i+1;
                    commandSender.sendMessage(a+". Name: " + plugin_title + "\nDescription: " + plugin_description);
                }
                commandSender.sendMessage("Install a plugin by using: /idk plugin install <name>");
            }
            catch (Exception e) {
                e.printStackTrace();
                commandSender.sendMessage("Error in getting from papermc, see console for more details.");
                commandSender.sendMessage("Switching to modrinth.");
                commandSender.sendMessage("Getting latest 10 plugins from modrinth...");
                int limit = 10;
                if (IDK.idk.debug) {
                    commandSender.sendMessage("limit="+limit);
                }
                String url = "https://api.modrinth.com/v2/search?limit="+limit+"&facets=";
                if (IDK.idk.debug) {
                    commandSender.sendMessage("url="+url);
                }
                String categories = "paper";
                if (IDK.idk.debug) {
                    commandSender.sendMessage("categories="+categories);
                }
                String versions = Bukkit.getServer().getMinecraftVersion();
                if (IDK.idk.debug) {
                    commandSender.sendMessage("versions="+versions);
                }
                String arg = "[[\"categories:"+categories+"\"],[\"versions:"+versions+"\"],[\"project_type:plugin\"]]";
                if (IDK.idk.debug) {
                    commandSender.sendMessage("arg="+arg);
                }
                String real_uri = argument_handler(url, arg);
                if (IDK.idk.debug) {
                    commandSender.sendMessage("real_uri="+real_uri);
                }
                String result = sendGet(real_uri);
                commandSender.sendMessage("10 Top Plugins:");
                for(int i = 0; i < 10; i++) {
                    String plugin_title = decoder.decode_json(result, i, "hits", "title");
                    String plugin_description = decoder.decode_json(result, i, "hits", "description");
                    String plugin_id = decoder.decode_json(result, i, "hits", "project_id");
                    int a = i+1;
                    commandSender.sendMessage(a+". Name: " + plugin_title + "\nID: " + plugin_id + "\nDescription: " + plugin_description);
                }
                commandSender.sendMessage("Install a plugin by using: /idk plugin install <name>");
            }
        }
        else {
            commandSender.sendMessage("Download source is invalid!");
        }
    }

    public static String convert_list_string_to_json_string(String list_string) {
        String fix_list_string = list_string.substring(1);
        return fix_list_string.substring(0, fix_list_string.length() - 1);
    }

    public static void install_project(CommandSender commandSender, String project_id) throws IOException {
        if (IDK.idk.debug) {
            if (commandSender instanceof Player) {
                commandSender.sendMessage("commandSender=Player");
            } else {
                commandSender.sendMessage("commandSender=Console");
            }
            commandSender.sendMessage("project_id="+project_id);
        }
        refresh_download_source();
        if (Objects.equals(download_source, "papermc")) {
            commandSender.sendMessage("Start finding plugin id: " + project_id + " from papermc.");
            //https://hangar.papermc.io/api/v1/projects/viaversion/versions
            int limit = 10;
            if (IDK.idk.debug) {
                commandSender.sendMessage("limit="+limit);
            }
            String version = Bukkit.getServer().getMinecraftVersion();
            if (IDK.idk.debug) {
                commandSender.sendMessage("version="+version);
            }
            String url = "https://hangar.papermc.io/api/v1/projects/{id|slug}?limit="+limit+"&version="+version;
            if (IDK.idk.debug) {
                commandSender.sendMessage("url="+url);
            }
            String real_uri = url.replace("{id|slug}", project_id);
            String result = sendGet(real_uri);
            String plugin_title = decoder.decode_json(result, "name");
            if (Bukkit.getPluginManager().getPlugin(plugin_title) != null) {
                commandSender.sendMessage("Plugin has already installed! No duplicate!");
            }
            else {
                try {
                    String new_url = "https://hangar.papermc.io/api/v1/projects/{id|slug}/versions?limit="+limit;
                    String new_real_uri = new_url.replace("{id|slug}",project_id);
                    String new_result = sendGet(new_real_uri);
                    String new_fixed_result = convert_list_string_to_json_string(decoder.decode_json(new_result, "result"));
                    String files = decoder.decode_json(new_fixed_result, "downloads");
                    String platform = "PAPER";
                    String fixed_file = decoder.decode_json(files, platform);
                    String file_url = decoder.decode_json(fixed_file, "downloadUrl");
                    String file_info = decoder.decode_json(fixed_file, "fileInfo");
                    String file_name = decoder.decode_json(file_info, "name");
                    String file_path = Bukkit.getPluginsFolder().getAbsolutePath()+"\\"+file_name;
                    downloadFileByUrl(plugin_title, file_url, file_path, file_name, commandSender);
                    enable_plugin(file_path, plugin_title, commandSender);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    commandSender.sendMessage("Plugin not found Or error was made!");
                }
            }
        }
        else if (Objects.equals(download_source, "modrinth")) {
            commandSender.sendMessage("Start finding plugin id: " + project_id + " from modrinth.");
            boolean featured = true;
            String loader = "paper";
            String game_version = Bukkit.getMinecraftVersion();
            String url = "https://api.modrinth.com/v2/project/{id|slug}?featured="+featured;
            String real_uri = url.replace("{id|slug}",project_id);
            String args = "&loaders=[\""+loader+"\"]&game_version=[\""+game_version+"\"]";
            String real_url = argument_handler(real_uri, args);
            String result = sendGet(real_url);
            String plugin_title = decoder.decode_json(result, "title");
            if (Bukkit.getPluginManager().getPlugin(plugin_title) != null) {
                commandSender.sendMessage("Plugin has already installed! No duplicate!");
            }
            else {
                try {
                    String new_url = "https://api.modrinth.com/v2/project/{id|slug}/version?";
                    String new_real_uri = new_url.replace("{id|slug}",project_id);
                    String new_args = "\"featured\"=\""+featured+"\"&loaders=[\""+loader+"\"]&game_version=[\""+game_version+"\"]";
                    String new_real_url = argument_handler(new_real_uri, new_args);
                    String new_result = sendGet(new_real_url);
                    String fixed_new_result = convert_list_string_to_json_string(new_result);
                    String files = decoder.decode_json(fixed_new_result, "files");
                    String fixed_files = convert_list_string_to_json_string(files);
                    String file_url = decoder.decode_json(fixed_files, "url");
                    String file_name = decoder.decode_json(fixed_files, "filename");
                    String file_path = Bukkit.getPluginsFolder().getAbsolutePath()+"\\"+file_name;
                    downloadFileByUrl(plugin_title, file_url, file_path, file_name, commandSender);
                    enable_plugin(file_path, plugin_title, commandSender);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    commandSender.sendMessage("Plugin not found Or error was made!");
                }
            }
        }
        else if (Objects.equals(download_source, "both")) {
            try {
                commandSender.sendMessage("Start finding plugin id: " + project_id + " from papermc.");
                //https://hangar.papermc.io/api/v1/projects/viaversion/versions
                int limit = 10;
                if (IDK.idk.debug) {
                    commandSender.sendMessage("limit="+limit);
                }
                String version = Bukkit.getServer().getMinecraftVersion();
                if (IDK.idk.debug) {
                    commandSender.sendMessage("version="+version);
                }
                String url = "https://hangar.papermc.io/api/v1/projects/{id|slug}?limit="+limit+"&version="+version;
                if (IDK.idk.debug) {
                    commandSender.sendMessage("url="+url);
                }
                String real_uri = url.replace("{id|slug}", project_id);
                String result = sendGet(real_uri);
                String plugin_title = decoder.decode_json(result, "name");
                if (Bukkit.getPluginManager().getPlugin(plugin_title) != null) {
                    commandSender.sendMessage("Plugin has already installed! No duplicate!");
                }
                else {
                    try {
                        String new_url = "https://hangar.papermc.io/api/v1/projects/{id|slug}/versions?limit="+limit;
                        String new_real_uri = new_url.replace("{id|slug}",project_id);
                        String new_result = sendGet(new_real_uri);
                        String new_fixed_result = convert_list_string_to_json_string(decoder.decode_json(new_result, "result"));
                        String files = decoder.decode_json(new_fixed_result, "downloads");
                        String platform = "PAPER";
                        String fixed_file = decoder.decode_json(files, platform);
                        String file_url = decoder.decode_json(fixed_file, "downloadUrl");
                        String file_info = decoder.decode_json(fixed_file, "fileInfo");
                        String file_name = decoder.decode_json(file_info, "name");
                        String file_path = Bukkit.getPluginsFolder().getAbsolutePath()+"\\"+file_name;
                        downloadFileByUrl(plugin_title, file_url, file_path, file_name, commandSender);
                        enable_plugin(file_path, plugin_title, commandSender);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        commandSender.sendMessage("Plugin not found Or error was made!");
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                commandSender.sendMessage("Error in getting from papermc, see console for more details.");
                commandSender.sendMessage("Switching to modrinth.");
                commandSender.sendMessage("Start finding plugin id: " + project_id + " from modrinth.");
                boolean featured = true;
                String loader = "paper";
                String game_version = Bukkit.getMinecraftVersion();
                String url = "https://api.modrinth.com/v2/project/{id|slug}?featured="+featured;
                String real_uri = url.replace("{id|slug}",project_id);
                String args = "&loaders=[\""+loader+"\"]&game_version=[\""+game_version+"\"]";
                String real_url = argument_handler(real_uri, args);
                String result = sendGet(real_url);
                String plugin_title = decoder.decode_json(result, "title");
                if (Bukkit.getPluginManager().getPlugin(plugin_title) != null) {
                    commandSender.sendMessage("Plugin has already installed! No duplicate!");
                }
                else {
                    try {
                        String new_url = "https://api.modrinth.com/v2/project/{id|slug}/version?";
                        String new_real_uri = new_url.replace("{id|slug}",project_id);
                        String new_args = "\"featured\"=\""+featured+"\"&loaders=[\""+loader+"\"]&game_version=[\""+game_version+"\"]";
                        String new_real_url = argument_handler(new_real_uri, new_args);
                        String new_result = sendGet(new_real_url);
                        String fixed_new_result = convert_list_string_to_json_string(new_result);
                        String files = decoder.decode_json(fixed_new_result, "files");
                        String fixed_files = convert_list_string_to_json_string(files);
                        String file_url = decoder.decode_json(fixed_files, "url");
                        String file_name = decoder.decode_json(fixed_files, "filename");
                        String file_path = Bukkit.getPluginsFolder().getAbsolutePath()+"\\"+file_name;
                        downloadFileByUrl(plugin_title, file_url, file_path, file_name, commandSender);
                        enable_plugin(file_path, plugin_title, commandSender);
                    }
                    catch (Exception e1) {
                        e1.printStackTrace();
                        commandSender.sendMessage("Plugin not found Or error was made!");
                    }
                }
            }
        }
        else {
            commandSender.sendMessage("Download source is invalid!");
        }
    }

    public static void enable_plugin(String file_path, String plugin_title, CommandSender commandSender) throws InvalidPluginException, InvalidDescriptionException {
        commandSender.sendMessage("Start loading plugin " + plugin_title);
        Bukkit.getPluginManager().loadPlugin(new File(file_path));
        commandSender.sendMessage("Plugin " + plugin_title + " loaded!");
        commandSender.sendMessage("Start enable plugin " + plugin_title);
        Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin(plugin_title));
        commandSender.sendMessage("Plugin " + plugin_title + "'s installation was complete!");
    }

    public static void get_projects_by_title(CommandSender commandSender, String title) throws IOException {
        if (IDK.idk.debug) {
            if (commandSender instanceof Player) {
                commandSender.sendMessage("commandSender=Player");
            } else {
                commandSender.sendMessage("commandSender=Console");
            }
            commandSender.sendMessage("title="+title);
        }
        refresh_download_source();
        if (Objects.equals(download_source, "papermc")) {
            commandSender.sendMessage("Trying to get " + title + " plugin's info from papermc...");
            int limit = 10;
            if (IDK.idk.debug) {
                commandSender.sendMessage("limit=" + limit);
            }
            String url = "https://hangar.papermc.io/api/v1/projects/{id/slug}?limit=" + limit;
            if (IDK.idk.debug) {
                commandSender.sendMessage("url=" + url);
            }
            String real_uri = url.replace("{id/slug}", title);
            if (IDK.idk.debug) {
                commandSender.sendMessage("real_uri=" + real_uri);
            }
            String result = sendGet(real_uri);
            int total_hits = decoder.get_total_hits_paper(result);
            if (IDK.idk.debug) {
                commandSender.sendMessage("total_hits=" + total_hits);
            }
            if (total_hits > limit) {
                for (int i = 0; i < limit; i++) {
                    String plugin_title = decoder.decode_json(result, i, "result", "title");
                    String plugin_description = decoder.decode_json(result, i, "result", "description");
                    int a = i + 1;
                    commandSender.sendMessage(a + ". Name: " + plugin_title + "\nDescription: " + plugin_description);
                    tell_raw(convert_to_minecraft_clickable_string(plugin_title), commandSender);
                }
            } else if (total_hits > 0) {
                for (int i = 0; i < total_hits; i++) {
                    String plugin_title = decoder.decode_json(result, i, "result", "title");
                    String plugin_description = decoder.decode_json(result, i, "result", "description");
                    int a = i + 1;
                    commandSender.sendMessage(a + ". Name: " + plugin_title + "\nDescription: " + plugin_description);
                    tell_raw(convert_to_minecraft_clickable_string(plugin_title), commandSender);
                }
            } else {
                commandSender.sendMessage("Error: Plugin not found or error was made! Check console messages if there's a error!");
            }
         }
         else if (Objects.equals(download_source, "modrinth")) {
             commandSender.sendMessage("Trying to get " + title + " plugin's info from modrinth...");
             int limit = 10;
             if (IDK.idk.debug) {
                 commandSender.sendMessage("limit=" + limit);
             }
             String url = "https://api.modrinth.com/v2/search?query=" + title + "&limit=" + limit + "&facets=";
             if (IDK.idk.debug) {
                 commandSender.sendMessage("url=" + url);
             }
             String categories = "paper";
             if (IDK.idk.debug) {
                 commandSender.sendMessage("categories=" + categories);
             }
             String versions = Bukkit.getServer().getMinecraftVersion();
             if (IDK.idk.debug) {
                 commandSender.sendMessage("versions=" + versions);
             }
             String arg = "[[\"categories:" + categories + "\"],[\"versions:" + versions + "\"],[\"project_type:plugin\"]]";
             if (IDK.idk.debug) {
                 commandSender.sendMessage("arg=" + arg);
             }
             String real_uri = argument_handler(url, arg);
             if (IDK.idk.debug) {
                 commandSender.sendMessage("real_uri=" + real_uri);
             }
             String result = sendGet(real_uri);
             int total_hits = decoder.get_total_hits(result);
             if (IDK.idk.debug) {
                 commandSender.sendMessage("total_hits=" + total_hits);
             }
             if (total_hits > limit) {
                 for (int i = 0; i < limit; i++) {
                     String plugin_title = decoder.decode_json(result, i, "hits", "title");
                     String plugin_description = decoder.decode_json(result, i, "hits", "description");
                     String plugin_id = decoder.decode_json(result, i, "hits", "project_id");
                     int a = i + 1;
                     commandSender.sendMessage(a + ". Name: " + plugin_title + "\nID: " + plugin_id + "\nDescription: " + plugin_description);
                     tell_raw(convert_to_minecraft_clickable_string(plugin_title), commandSender);
                 }
             } else if (total_hits > 0) {
                 for (int i = 0; i < total_hits; i++) {
                     String plugin_title = decoder.decode_json(result, i, "hits", "title");
                     String plugin_description = decoder.decode_json(result, i, "hits", "description");
                     String plugin_id = decoder.decode_json(result, i, "hits", "project_id");
                     int a = i + 1;
                     commandSender.sendMessage(a + ". Name: " + plugin_title + "\nID: " + plugin_id + "\nDescription: " + plugin_description);
                     tell_raw(convert_to_minecraft_clickable_string(plugin_title), commandSender);
                 }
             } else {
                 commandSender.sendMessage("Error: Plugin not found or error was made! Check console messages if there's a error!");
             }
         }
         else if (Objects.equals(download_source, "both")) {
             try {
                 commandSender.sendMessage("Trying to get " + title + " plugin's info from papermc...");
                 int limit = 10;
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("limit=" + limit);
                 }
                 String url = "https://hangar.papermc.io/api/v1/projects/{id/slug}?limit=" + limit;
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("url=" + url);
                 }
                 String real_uri = url.replace("{id/slug}", title);
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("real_uri=" + real_uri);
                 }
                 String result = sendGet(real_uri);
                 int total_hits = decoder.get_total_hits_paper(result);
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("total_hits=" + total_hits);
                 }
                 if (total_hits > limit) {
                     for (int i = 0; i < limit; i++) {
                         String plugin_title = decoder.decode_json(result, i, "result", "title");
                         String plugin_description = decoder.decode_json(result, i, "result", "description");
                         int a = i + 1;
                         commandSender.sendMessage(a + ". Name: " + plugin_title + "\nDescription: " + plugin_description);
                         tell_raw(convert_to_minecraft_clickable_string(plugin_title), commandSender);
                     }
                 } else if (total_hits > 0) {
                     for (int i = 0; i < total_hits; i++) {
                         String plugin_title = decoder.decode_json(result, i, "result", "title");
                         String plugin_description = decoder.decode_json(result, i, "result", "description");
                         int a = i + 1;
                         commandSender.sendMessage(a + ". Name: " + plugin_title + "\nDescription: " + plugin_description);
                         tell_raw(convert_to_minecraft_clickable_string(plugin_title), commandSender);
                     }
                 } else {
                     commandSender.sendMessage("Error: Plugin not found or error was made! Check console messages if there's a error!");
                 }
             }
             catch (Exception e) {
                 e.printStackTrace();
                 commandSender.sendMessage("An error was made while getting from papermc.");
                 commandSender.sendMessage("Switching to modrinth...");
                 commandSender.sendMessage("Trying to get " + title + " plugin's info from modrinth...");
                 int limit = 10;
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("limit=" + limit);
                 }
                 String url = "https://api.modrinth.com/v2/search?query=" + title + "&limit=" + limit + "&facets=";
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("url=" + url);
                 }
                 String categories = "paper";
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("categories=" + categories);
                 }
                 String versions = Bukkit.getServer().getMinecraftVersion();
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("versions=" + versions);
                 }
                 String arg = "[[\"categories:" + categories + "\"],[\"versions:" + versions + "\"],[\"project_type:plugin\"]]";
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("arg=" + arg);
                 }
                 String real_uri = argument_handler(url, arg);
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("real_uri=" + real_uri);
                 }
                 String result = sendGet(real_uri);
                 int total_hits = decoder.get_total_hits(result);
                 if (IDK.idk.debug) {
                     commandSender.sendMessage("total_hits=" + total_hits);
                 }
                 if (total_hits > limit) {
                     for (int i = 0; i < limit; i++) {
                         String plugin_title = decoder.decode_json(result, i, "hits", "title");
                         String plugin_description = decoder.decode_json(result, i, "hits", "description");
                         String plugin_id = decoder.decode_json(result, i, "hits", "project_id");
                         int a = i + 1;
                         commandSender.sendMessage(a + ". Name: " + plugin_title + "\nID: " + plugin_id + "\nDescription: " + plugin_description);
                         tell_raw(convert_to_minecraft_clickable_string(plugin_title), commandSender);
                     }
                 } else if (total_hits > 0) {
                     for (int i = 0; i < total_hits; i++) {
                         String plugin_title = decoder.decode_json(result, i, "hits", "title");
                         String plugin_description = decoder.decode_json(result, i, "hits", "description");
                         String plugin_id = decoder.decode_json(result, i, "hits", "project_id");
                         int a = i + 1;
                         commandSender.sendMessage(a + ". Name: " + plugin_title + "\nID: " + plugin_id + "\nDescription: " + plugin_description);
                         tell_raw(convert_to_minecraft_clickable_string(plugin_title), commandSender);
                     }
                 } else {
                     commandSender.sendMessage("Error: Plugin not found or error was made! Check console messages if there's a error!");
                 }
             }
         }
         else {
             commandSender.sendMessage("Download source is invalid!");
         }
    }

    public static void tell_raw(String string, CommandSender commandSender) {
        if(commandSender instanceof Player) {
            ((Player) commandSender).performCommand("tellraw @s "+string);
        } else {

        }
    }

    public static String convert_to_minecraft_clickable_string(String plugin_id) {
        // /tellraw @s
        // {"text":"Install this plugin by click ",
        // "extra":[{"clickEvent":{"action":"run_command","value":"/kill"},
        // "hoverEvent":{"action":"show_text","contents":"Click here to install"},
        // "text":"[install]","color":"blue"},
        // {"text":" or type /kill"}]}
        String notice1 = idkMessageConfig.getString("install_plugin_notice1");
        String notice2 = idkMessageConfig.getString("install_plugin_notice2").replace("[plugin_id]", plugin_id);
        String install_button = idkMessageConfig.getString("install_button");
        String install_button_hover = idkMessageConfig.getString("install_button_hover");
        return "{\"text\":\""+notice1+" "+"\",\"extra\":[" +
                "{\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/idk plugin install "+plugin_id+"\"}," +
                "\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\""+install_button_hover+"\"}," +
                "\"text\":\""+install_button+"\",\"color\":\"blue\"},{\"text\":\""+" "+notice2+"\"}]}";
    }

    public static String argument_handler(String url, String arg) throws UnsupportedEncodingException {
        String encode_arg = URLEncoder.encode(arg, "UTF-8");
        String real_uri = url+encode_arg;
        return real_uri;
    }

    private static final CloseableHttpClient httpclient = HttpClients.createDefault();

    public static String sendGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void get_10_top_projects_a(CommandSender commandSender) throws IOException {
        new Thread(() -> {
            try {
                get_10_top_projects(commandSender);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).run();
    }

    public static void downloadFileByUrl(String plugin_title, String urlPath, String downloadPath, String fileName, CommandSender commandSender) throws IOException {
        try {
            commandSender.sendMessage(idkMessageConfig.getString("download_start").replace("%filename%", fileName).replace("%url%", urlPath));
            File file = new File(downloadPath);
            if(file.exists()) {
                if(Bukkit.getPluginManager().getPlugin(plugin_title) != null) {
                    if(Bukkit.getPluginManager().getPlugin(plugin_title).isEnabled()) {
                        return;
                    } else{
                        Plugin plugin = Bukkit.getPluginManager().getPlugin(plugin_title);
                        Bukkit.getPluginManager().enablePlugin(plugin);
                        return;
                    }
                } else {
                    file.delete();
                }
            }
            URL url = new URL(urlPath);
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            conn.setConnectTimeout(3 * 1000);
            FileOutputStream fos = new FileOutputStream(downloadPath);
            IDKnetHandler pbt = new IDKnetHandler((int) conn.getContentLength(), 1L, "000001");//创建进度条
            new Thread(pbt).start();//开启线程，刷新进度条
            byte[] buf = new byte[1024];
            int size = 0;
            pbt.set_env(null, true);
            while ((size = inStream.read(buf)) != -1) {//循环读取
                fos.write(buf, 0, size);
                pbt.updateProgress(size);//写完一次，更新进度条
            }
            pbt.finish();//文件读取完成，关闭进度条
            fos.flush();
            fos.close();
            commandSender.sendMessage(idkMessageConfig.getString("download_complete2").replace("%path%", downloadPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void get_projects_by_title_a(CommandSender commandSender, String title) throws IOException {
        new Thread(() -> {
            try {
                get_projects_by_title(commandSender, title);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).run();
    }

    private ArrayList<Integer> proList = new ArrayList<Integer>();
    private int progress;//当前进度
    private int totalSize;//下载文法总大小
    private boolean run = true;
    private int showProgress;
    public static HashMap<String, Integer> progressMap = new HashMap<>();//各用户的下载进度
    public static HashMap<String, Boolean> executeStatusMap = new HashMap<>();//各用户是否下载中
    private Long fileId;
    private String token;
    private boolean is_a_console;
    private Player player;

    public IDKnetHandler(int totalSize, Long fileId, String token) {
        this.totalSize = totalSize;
        this.fileId = fileId;
        this.token = token;
        //创建进度条时，将指定用户的执行状态改为true
        executeStatusMap.put(token, true);
    }

    public void updateProgress(int progress) {
        synchronized (this.proList) {
            if (this.run) {
                this.proList.add(progress);
                this.proList.notify();
            }
        }
    }

    public void finish() {
        this.run = false;
        //关闭进度条
    }

    public void set_env(Player player, boolean is_a_console) {
        this.player = player;
        this.is_a_console = is_a_console;
    }

    @Override
    public void run() {
        synchronized (this.proList) {
            try {
                while (this.run) {
                    if (this.proList.size() == 0) {
                        this.proList.wait();
                    }
                    synchronized (proList) {
                        this.progress += this.proList.remove(0);
                        //更新进度条
                        showProgress = (int) ((new BigDecimal((float) this.progress / this.totalSize).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()) * 100);
                        progressMap.put(token + fileId, showProgress);
                        if (showProgress == 100) {
                            //进度100%时将用户的执行状态改为false
                            executeStatusMap.put(token, false);
                        }
                        if (is_a_console){
                            System.err.println(idkMessageConfig.getString("current_progress").replace("%progress%", String.valueOf(showProgress)));
                            //System.err.println("Current Progress："+showProgress+"%");
                        } else {
                            player.sendMessage(idkMessageConfig.getString("current_progress").replace("%progress%", String.valueOf(showProgress)));
                            //player.sendMessage("Current Progress："+showProgress+"%");
                        }
                    }
                }
                if (is_a_console){
                    System.out.println(idkMessageConfig.getString("download_complete"));
                    //System.err.println("Download Complete!");
                } else {
                    player.sendMessage(idkMessageConfig.getString("download_complete"));
                    //player.sendMessage("Download Complete!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}