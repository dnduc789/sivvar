/*
videostarter.c

mediastreamer2 library - modular sound and video processing and streaming
Copyright (C) 2011-2012 Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/


#include <mediastreamer2/videostarter.h>


void ms_video_starter_init(MSVideoStarter *vs) {
	vs->next_time = 0;
	vs->i_frame_count = 0;
}

void ms_video_starter_first_frame(MSVideoStarter *vs, uint64_t curtime) {
	vs->next_time = curtime + 2000;
}

bool_t ms_video_starter_need_i_frame(MSVideoStarter *vs, uint64_t curtime) {
	if (vs->next_time == 0) return FALSE;
	if (curtime >= vs->next_time) {
		vs->i_frame_count++;
		if (vs->i_frame_count == 1) {
			vs->next_time += 2000;
		} else {
			vs->next_time = 0;
		}
		return TRUE;
	}
	return FALSE;
}
