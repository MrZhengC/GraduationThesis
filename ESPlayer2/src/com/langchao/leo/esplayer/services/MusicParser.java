package com.langchao.leo.esplayer.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v24FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.utils.MD5;

/**
 * 音乐解析器，用来获取音乐的信息（现只支持MP3格式）
 * 
 * @author 碧空
 * 
 */
public class MusicParser {
	
	/**
	 * 获取MP3信息
	 * @param path
	 * @return
	 */
	public static @Nullable RealSong getMp3MusicInfo(String path){
		if (TextUtils.isEmpty(path)){
			return null;
		}
		RealSong realSong = new RealSong(new File(path));
		getMp3MusicInfo(realSong);
		return realSong;  
	}
	/**
	 * 获取MP3信息
	 * @param path
	 * @return
	 */
	public static @Nullable RealSong getMp3MusicInfo(RealSong realSong){
		if (realSong == null){
			return null;
		}
		
		try {
			MP3File mp3File = new MP3File(realSong.getLocalPath());  
			
			MP3AudioHeader header = mp3File.getMP3AudioHeader(); // mp3文件头部信息
			realSong.setDuration(header.getTrackLength());
			
			if (mp3File.hasID3v1Tag()) {
				Tag tag = mp3File.getTag();
				realSong.setArtist("" + tag.getFirst(FieldKey.ARTIST));
				realSong.setAlbum("" + tag.getFirst(FieldKey.ALBUM));
				realSong.setSongName("" + tag.getFirst(FieldKey.TITLE));
				
				// 封面图片
				Artwork artwork = tag.getFirstArtwork(); // 获得第一张专辑图片
				if (artwork != null){
					byte[] byteArray = artwork.getBinaryData(); // 将读取到的专辑图片转成二进制
					if (byteArray != null){
						Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
						// 将bitmap保存到SDcard中,使用MP3文件名的MD5值作为保存的名称
						String coverUrl = saveBitmap(MD5.md5(realSong.getLocalPath()), bitmap);
						
						realSong.setSmallSongPic(coverUrl);
						realSong.setBigSongPic(coverUrl);
						
						bitmap.recycle();
					}
				}
			}
			
			if (mp3File.hasID3v2Tag()) {
				ID3v24Tag tagV24 = mp3File.getID3v2TagAsv24();
				realSong.setSongName("" + tagV24.getFirst(ID3v24FieldKey.TITLE));
				realSong.setArtist("" + tagV24.getFirst(ID3v24FieldKey.ARTIST));
				realSong.setAlbum("" + tagV24.getFirst(ID3v24FieldKey.ALBUM));
			}
			
		} catch (IOException e) {  
			e.printStackTrace();  
		} catch (TagException e) {  
			e.printStackTrace();  
		} catch (ReadOnlyFileException e) {  
			e.printStackTrace();  
		} catch (InvalidAudioFrameException e) {  
			e.printStackTrace();  
		}
		return realSong;  
	}
	
	/**
	 * 获得歌曲内容
	 */
	static void getContent(File file) {
		try {
			MP3File mp3File = (MP3File) AudioFileIO.read(file);
			if (mp3File.hasID3v1Tag()) {
				Tag tag = mp3File.getTag();
				StringBuffer sbf = new StringBuffer();
				sbf.append("歌手：" + tag.getFirst(FieldKey.ARTIST) + "\n");
				sbf.append("专辑名：" + tag.getFirst(FieldKey.ALBUM) + "\n");
				sbf.append("歌名：" + tag.getFirst(FieldKey.TITLE) + "\n");
				sbf.append("年份：" + tag.getFirst(FieldKey.YEAR));

				System.out.println("mp3file: "+sbf.toString());
				
				Artwork artwork = tag.getFirstArtwork(); // 获得第一张专辑图片
				if (artwork != null){
					byte[] byteArray = artwork.getBinaryData(); // 将读取到的专辑图片转成二进制
					Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
					
					// 将bitmap保存到SDcard中,使用MP3文件名的MD5值作为保存的名称
					saveBitmap(MD5.md5(file.getName()), bitmap);
				}
			}
			if (mp3File.hasID3v2Tag()) {
				AbstractID3v2Tag tagV2 = mp3File.getID3v2Tag();  
				AbstractID3v2Frame frame = (AbstractID3v2Frame) tagV2.getFrame("APIC");
				if (frame != null) {
					FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();  
					byte[] imageData = body.getImageData(); 
					// 通过BitmapFactory转成Bitmap
					Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
					// 将bitmap保存到SDcard中,使用MP3文件名的MD5值作为保存的名称
					saveBitmap(MD5.md5(file.getName()), bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得头部信息
	 */
	static void getHead(File file) {
		try {
			MP3File mp3File = (MP3File) AudioFileIO.read(file);
			MP3AudioHeader header = mp3File.getMP3AudioHeader(); // mp3文件头部信息
			StringBuffer sbf = new StringBuffer();
			sbf.append("长度: " + header.getTrackLength() + "\n");
			sbf.append("比特率: " + header.getBitRate() + "\n");
			sbf.append("编码器: " + header.getEncoder() + "\n");
			sbf.append("格式: " + header.getFormat() + "\n");
			sbf.append("声道: " + header.getChannels() + "\n");
			sbf.append("采样率: " + header.getSampleRate() + "\n");
			sbf.append("MPEG: " + header.getMpegLayer() + "\n");
			sbf.append("MP3起始字节: " + header.getMp3StartByte() + "\n");
			sbf.append("精确的长度: " + header.getPreciseTrackLength() + "\n");
			sbf.append("帧数：" + header.getNumberOfFrames() + "\n");
			sbf.append("编码类型：" + header.getEncodingType() + "\n");
			sbf.append("MPEG版本:" + header.getMpegVersion() + "\n");
			
			System.out.println("mp3file: "+sbf.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String saveBitmap(String bitmapName, Bitmap mBitmap) {
		
		File folder = new File(Constants.APP_COVER_IMAGE_FOLDER);
		if (!folder.exists()){
			folder.mkdirs();
		}
		
		File f = new File(Constants.APP_COVER_IMAGE_FOLDER + bitmapName);
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 使用jpg编码压缩
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f.getAbsolutePath();
	}

}
