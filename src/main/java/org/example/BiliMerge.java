package org.example;

import org.example.BiliName;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BiliMerge {

    int flag=1;

//    String type="坠子";
//    String type="大弦戏";
//    String type="大平调";
//    String type="二夹弦";
//    String type="曲剧";
//    String type="豫剧";
    String type="戏曲";

    public static void main(String[] args) throws IOException {
//        bilibili视频缓存所在的文件夹,自动递归查找文件夹
        String inFile = "E:\\x";
//        ffmpeg.exe 的所在目录
        String ffmpegFile = "E:\\ffmpeg-7.0.2-essentials_build\\ffmpeg-7.0.2-essentials_build\\bin\\ffmpeg.exe";
//        输出目录
        String outFile = "E:\\X3\\";


//        String ffmpegFile = "C:\\xi\\ffmpeg\\bin\\ffmpeg.exe";
//        String inFile = "C:\\xi\\uwp";
//        String outFile = "C:\\xi\\mp4\\";


        BiliMerge biliMerge = new BiliMerge();
        biliMerge.runffmpeg(inFile, outFile, ffmpegFile);

    }
    Pattern video = Pattern.compile(".*0\\d{2}\\.m4s$");
    Pattern audio = Pattern.compile(".*2\\d{2}\\.m4s$");
    public void runffmpeg(String inFile, String outFile, String ffmpegFile) {
        File file = new File(inFile);
        int i =0;
        List<BiliName> list = new ArrayList<>();
        if (file.isDirectory()) {
            String[] directoryList = file.list();
            if (ArrayUtils.isNotEmpty(directoryList)) {
                for (String directory : directoryList) {
                    //拿到150个文件夹
                    File file1 = new File(file, directory);
                    //150个文件夹遍历里面的对象
                    if (!file1.isDirectory()) {
                        continue;
                    }
                    for (String fileName : file1.list()) {

                        if (".videoInfo".equals(fileName)) {
                            File videoInfoFile = new File(file1, fileName);
                            readerName(directory, videoInfoFile,list);
                        }

                        if (audio.matcher(fileName).matches() ) {
                            BiliName biliName = findBiliById(file1.getName(),list);
                            if (audio.matcher(fileName).matches()) {
                                biliName.setAudioFile(file1+"\\"+fileName);
                            }

                        }
                        if (video.matcher(fileName).matches()){
                            BiliName biliName = findBiliById(file1.getName(), list);
                            if (video.matcher(fileName).matches()) {
                                biliName.setVideoFile(file1+"\\"+fileName);
                            }
                        }
                    }
                    i++;
                    System.out.println(i);
                    if (list.size()>0){
                        //第一步：递归拿到所有的分P视频对象
                        //第二步：循环处理每一个对象(获取标题name)
                        //第三步：针对于每一个对象，前九位如果是连续的30就清空，覆盖源文件。
                        //第四步：组装命令，inVideo，inAudio，out，ext位置

                        list.stream().parallel()
                                .filter(biliName -> !new File(new StringBuilder(outFile).append(biliName.getName()).append(".mp4").toString()).isFile())
                                .forEach(biliName ->
                                        {
                                            if (StringUtils.isNotEmpty(biliName.getAudioFile()) && StringUtils.isNotEmpty(biliName.getVideoFile())) {
                                                ffmpegUtil(biliName.getVideoFile(), biliName.getAudioFile(), new StringBuilder(outFile).append(biliName.getName()).append(".mp4").toString(),
                                                        ffmpegFile);
                                                System.out.println(biliName.getName() + ",转换成功");
                                            } else {
                                                if (null == biliName.getVideoFile()) {
                                                    System.err.println("编号id为：" + biliName.getId() + " 输入的video.m4s 不存在" + ".详细信息为： " + biliName);
                                                }
                                                if (null == biliName.getAudioFile()) {
                                                    System.err.println("编号id为：" + biliName.getId() + " 输入的audio.m4s 不存在" + ".详细信息为： " + biliName);
                                                }
                                            }
                                        }
                                );
                        list=new ArrayList<>();
                    }


                }
            }
        }

    }



//    public void fileName(File file) {
//        if (file.isDirectory()) {
//            String[] directoryList = file.list();
//            if (ArrayUtils.isNotEmpty(directoryList)) {
//                for (String directory : directoryList) {
//                    fileName(new File(file, directory));
//                }
//            }
//        }
//        //1. 获取文件名字
//        String fileName = file.getName();
//        if (file.isFile()) {
//            if (".videoInfo".equals(fileName)) {
//                String id = file.getParentFile().getName();
//                readerName(id, file);
//            }
//
//            if (audio.matcher(fileName).matches() || video.matcher(fileName).matches()) {
//                BiliName biliName = findBiliById(file.getParentFile().getName());
//                if (audio.matcher(fileName).matches()) {
//                    biliName.setAudioFile(file.getPath());
//                }
//                if (video.matcher(fileName).matches()) {
//                    biliName.setVideoFile(file.getPath());
//                }
//            }
//        }
//    }

    public void fileName(File file) {


    }

    private BiliName findBiliById(String id, List<BiliName> list) {
        if (list != null) {
            for (BiliName biliName : list) {
                if (biliName != null && id.equals(biliName.getId())) {
                    return biliName;
                }
            }
        }
        BiliName biliName = new BiliName();
        biliName.setId(id);
        biliName.setName(id);
        list.add(biliName);
        return biliName;
    }



    private void setName(String id, String avid, String parentName, String name, List<BiliName> list) {

        BiliName biliName = new BiliName();
        if (list != null) {
            for (BiliName biliName2 : list) {
                if (biliName2 != null && id.equals(biliName2.getId())) {
                    biliName=biliName2;
                    break;
                }
            }
        }
        biliName.setId(id);
        biliName.setParentName(parentName);


        flag++;
        biliName.setName(flag+"-"+type+"-"+name);


//        String[] split = name.split("-");
//        String[] split1 = split[1].split(".mp4");
//        biliName.setName(split1[0]+"-"+type+"-"+name);
        biliName.setAvid(avid);
        list.add(biliName);

    }

    private void readerName(String id, File file, List<BiliName> list) {
        BufferedReader bufferedReader = null;
        FileInputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            String jsonStr = sb.toString();
            Map map = JSON.parseObject(jsonStr, Map.class);
            if (map.containsKey("title") && map.containsKey("bvid")) {
                String title = (String) map.get("title");
                String avid = (String) map.get("bvid");
                Integer p = (Integer) map.get("p");
                String cid = String.valueOf(map.get("cid"));
                setName(id, avid,cid, title+"-"+p,list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void ffmpegUtil(String videoFile, String audioFile, String outFile, String FFmpeg) {
        if (videoFile == null || audioFile == null || outFile == null || FFmpeg == null) {
            return;
        } else {
            removeConsecutiveZeros(videoFile);
            removeConsecutiveZeros(audioFile);
            ProcessBuilder processBuilder = new ProcessBuilder();
            List<String> command = new ArrayList<>();
            command.add(FFmpeg);
            command.add("-i");
            command.add(videoFile);
            command.add("-i");
            command.add(audioFile);
            command.add("-codec");
            command.add("copy");
            command.add(outFile);
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);
            Process process = null;
            try {

                process = processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void removeConsecutiveZeros(String filePath) {
        File file = new File(filePath); // 替换为你的文件路径

        // 读取文件到字节数组
        byte[] originalBytes = new byte[0];
        try {
            originalBytes = Files.readAllBytes(file.toPath());

            // 处理字节数组：检查前九个字节是否连续都是30（十进制的0），如果是则清空并保留第10位开始的数据
            int index = 0;
            for (int i = 0; i < Math.min(9, originalBytes.length); i++) {
                if (originalBytes[i] != 48) { // 检查当前字节是否是30（即0）
                    break;
                }
                index++;
            }

            // 创建一个新的字节数组，只包含非连续30的部分
            byte[] modifiedBytes;
            if (index == 9 && originalBytes.length > 9) {
                modifiedBytes = new byte[originalBytes.length - 9];
                System.arraycopy(originalBytes, 9, modifiedBytes, 0, originalBytes.length - 9);
            } else {
                modifiedBytes = originalBytes;
            }

            // 将处理后的字节数组写回原文件
            Files.write(file.toPath(), modifiedBytes);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}